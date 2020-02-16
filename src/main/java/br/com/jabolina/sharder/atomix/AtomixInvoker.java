package br.com.jabolina.sharder.atomix;

import br.com.jabolina.sharder.cluster.node.Node;
import br.com.jabolina.sharder.concurrent.ConcurrentContext;
import br.com.jabolina.sharder.exception.Error;
import br.com.jabolina.sharder.exception.SharderRuntimeException;
import br.com.jabolina.sharder.message.AbstractSharderMessageResponse;
import br.com.jabolina.sharder.message.SharderMessageResponse;
import br.com.jabolina.sharder.message.atomix.operation.AbstractAtomixOperation;
import br.com.jabolina.sharder.message.atomix.operation.ExecuteOperation;
import br.com.jabolina.sharder.message.atomix.operation.QueryOperation;
import br.com.jabolina.sharder.message.atomix.request.AtomixExecuteRequest;
import br.com.jabolina.sharder.message.atomix.request.AtomixQueryRequest;
import br.com.jabolina.sharder.message.atomix.response.AtomixExecuteResponse;
import br.com.jabolina.sharder.message.atomix.response.AtomixQueryResponse;
import com.google.common.collect.Maps;

import java.net.ConnectException;
import java.nio.channels.ClosedChannelException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * @author jabolina
 * @date 2/9/20
 */
final class AtomixInvoker {
  private static final int MAX_ATTEMPTS = 30;
  private static final int[] FIBONACCI_BACKOFF = new int[]{1, 1, 2, 3, 5};
  private static final Predicate<Throwable> THROWABLE_PREDICATE = e ->
      e instanceof ConnectException
          || e instanceof TimeoutException
          || e instanceof ClosedChannelException;
  private final AtomixSessionConnection sessionConnection;
  private final ConcurrentContext context;
  private final Map<String, ActionAttempt> attempts = Maps.newConcurrentMap();

  AtomixInvoker(Node node, ConcurrentContext context) {
    this.sessionConnection = new AtomixSessionConnection(node.atomix(), context);
    this.context = context;
  }

  public CompletableFuture<AtomixExecuteResponse> invoke(ExecuteOperation operation) {
    CompletableFuture<AtomixExecuteResponse> future = new CompletableFuture<>();
    invoke(new ExecuteAttempt(1, operation, future));
    return future;
  }

  public CompletableFuture<AtomixQueryResponse> invoke(QueryOperation operation) {
    CompletableFuture<AtomixQueryResponse> future = new CompletableFuture<>();
    invoke(new QueryAttempt(1, operation, future));
    return future;
  }

  private <T extends AbstractAtomixOperation, U extends AbstractSharderMessageResponse> void invoke(ActionAttempt<T, U> attempt) {
    if (attempt.attempt < MAX_ATTEMPTS) {
      // FIXME operation sequence
      attempts.put(attempt.operation.primitive().primitiveName(), attempt);
      attempt.send();
      attempt.future.whenComplete((res, err) -> attempts.remove(attempt.operation.primitive().primitiveName()));
    } else {
      attempt.fail(Error.TIMEOUT.exception());
    }
  }

  private abstract class ActionAttempt<T extends AbstractAtomixOperation, U extends AbstractSharderMessageResponse> implements BiConsumer<U, Throwable> {
    protected final int attempt;
    protected final T operation;
    protected final CompletableFuture<U> future;

    ActionAttempt(int attempt, T operation, CompletableFuture<U> future) {
      this.attempt = attempt;
      this.operation = operation;
      this.future = future;
    }

    /**
     * Sends the attempt.
     */
    protected abstract void send();

    /**
     * Returns the next instance of the attempt.
     *
     * @return The next instance of the attempt.
     */
    protected abstract ActionAttempt<T, U> next();

    /**
     * Returns a new instance of the default exception for the operation.
     *
     * @return A default exception for the operation.
     */
    protected abstract Throwable exception();

    /**
     * Completes the operation successfully.
     *
     * @param response The operation response.
     */
    protected abstract void complete(U response);

    @Override
    public void accept(U response, Throwable throwable) {
      if (throwable == null) {
        if (response.status().equals(SharderMessageResponse.Status.OK)) {
          complete(response);
        }
        // Some problem while executing command, retry now
        else if (response.error().error().equals(Error.FAILURE)) {
          retry();
        }
        // Could not find client to execute or the usage is wrong
        else if (response.error().error().equals(Error.UNAVAILABLE) || response.error().error().equals(Error.WRONG_USAGE)) {
          complete(response.error().exception());
        }
        // Could not execute command right now
        else if (response.error().error().equals(Error.TIMEOUT) || response.error().error().equals(Error.NOT_READY)) {
          retry(Duration.ofSeconds(FIBONACCI_BACKOFF[Math.min(attempt - 1, FIBONACCI_BACKOFF.length - 1)]));
        } else {
          complete(response.error().exception());
        }
      } else if (THROWABLE_PREDICATE.test(throwable)
          || throwable instanceof CompletionException && THROWABLE_PREDICATE.test(throwable.getCause())) {
        retry(Duration.ofSeconds(FIBONACCI_BACKOFF[Math.min(attempt - 1, FIBONACCI_BACKOFF.length - 1)]));
      } else {
        fail(throwable);
      }
    }

    /**
     * Completes the operation with an exception.
     *
     * @param error The completion exception.
     */
    protected void complete(Throwable error) {
      future.completeExceptionally(error);
    }

    /**
     * Fails the attempt.
     */
    public void fail() {
      fail(exception());
    }

    /**
     * Fails the attempt with the given exception.
     *
     * @param t The exception with which to fail the attempt.
     */
    public void fail(Throwable t) {
      complete(t);
    }

    /**
     * Immediately retries the attempt.
     */
    public void retry() {
      invoke(next());
    }

    /**
     * Retries the attempt after the given duration.
     *
     * @param delay The duration after which to retry the attempt.
     */
    public void retry(Duration delay) {
      context.schedule(delay, this::retry);
    }
  }

  /**
   * Attempt to execute something on Atomix.
   * </p>
   * Handle an execute request and response, retrying and finishing when is needed.
   */
  private final class ExecuteAttempt extends ActionAttempt<ExecuteOperation, AtomixExecuteResponse> {

    protected ExecuteAttempt(int attempt, ExecuteOperation operation, CompletableFuture<AtomixExecuteResponse> future) {
      super(attempt, operation, future);
    }

    @Override
    protected void send() {
      sessionConnection.execute(AtomixExecuteRequest.builder()
          .withOperation(operation)
          .build()).whenCompleteAsync(this, context);
    }

    @Override
    protected ActionAttempt<ExecuteOperation, AtomixExecuteResponse> next() {
      return new ExecuteAttempt(attempt + 1, operation, future);
    }

    @Override
    protected Throwable exception() {
      return new SharderRuntimeException.ExecutionFailure("Failed to execute action!");
    }

    @Override
    protected void complete(AtomixExecuteResponse response) {
      future.complete(response);
    }
  }

  /**
   * Attempt to query something on Atomix.
   * </p>
   * Handle an query request and response, retrying and finishing when is needed.
   */
  private final class QueryAttempt extends ActionAttempt<QueryOperation, AtomixQueryResponse> {

    protected QueryAttempt(int attempt, QueryOperation operation, CompletableFuture<AtomixQueryResponse> future) {
      super(attempt, operation, future);
    }

    @Override
    protected void send() {
      sessionConnection.query(AtomixQueryRequest.builder()
          .withOperation(operation)
          .build()).whenCompleteAsync(this, context);
    }

    @Override
    protected ActionAttempt<QueryOperation, AtomixQueryResponse> next() {
      return new QueryAttempt(attempt + 1, operation, future);
    }

    @Override
    protected Throwable exception() {
      return new SharderRuntimeException.ExecutionFailure("Failed to query action!");
    }

    @Override
    protected void complete(AtomixQueryResponse response) {
      future.complete(response);
    }
  }
}
