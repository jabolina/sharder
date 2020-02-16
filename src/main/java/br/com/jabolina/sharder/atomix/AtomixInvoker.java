package br.com.jabolina.sharder.atomix;

import br.com.jabolina.sharder.cluster.node.Node;
import br.com.jabolina.sharder.concurrent.ConcurrentContext;
import br.com.jabolina.sharder.exception.Error;
import br.com.jabolina.sharder.exception.SharderRuntimeException;
import br.com.jabolina.sharder.message.AbstractSharderMessageResponse;
import br.com.jabolina.sharder.message.atomix.operation.AbstractAtomixOperation;
import br.com.jabolina.sharder.message.atomix.operation.ExecuteOperation;
import br.com.jabolina.sharder.message.atomix.operation.QueryOperation;
import br.com.jabolina.sharder.message.atomix.request.AtomixExecuteRequest;
import br.com.jabolina.sharder.message.atomix.request.AtomixQueryRequest;
import br.com.jabolina.sharder.message.atomix.response.AtomixExecuteResponse;
import br.com.jabolina.sharder.message.atomix.response.AtomixQueryResponse;
import com.google.common.collect.Maps;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * @author jabolina
 * @date 2/9/20
 */
final class AtomixInvoker {
  private final static int MAX_ATTEMPTS = 30;
  private final AtomixSessionConnection sessionConnection;
  private final ConcurrentContext context;
  private final Map<String, ActionAttempt> attempts = Maps.newConcurrentMap();

  AtomixInvoker(Node node, ConcurrentContext context) {
    this.sessionConnection = new AtomixSessionConnection(node.atomix(), context);
    this.context = context;
  }

  public CompletableFuture<AtomixExecuteResponse> invoke(ExecuteOperation operation) {
    CompletableFuture<AtomixExecuteResponse> future = new CompletableFuture<>();
    invoke(new ExecuteAttempt(0, operation, future));
    return future;
  }

  public CompletableFuture<AtomixQueryResponse> invoke(QueryOperation operation) {
    CompletableFuture<AtomixQueryResponse> future = new CompletableFuture<>();
    invoke(new QueryAttempt(0, operation, future));
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

    /**
     * Completes the operation with an exception.
     *
     * @param error The completion exception.
     */
    protected void complete(Throwable error) {

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
      future.completeExceptionally(t);
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

    @Override
    public void accept(AtomixExecuteResponse response, Throwable throwable) {
      System.out.println("RECEVIED EXECUTE RESPONSE1");
      if (throwable == null) {
        if (response != null) {
          System.out.println("RECEVIED EXECUTE RESPONSE ");
          complete(response);
        } else {
          System.out.println("response is null!!!!!");
        }
      } else {
        System.out.println("throwable is not null!!!");
        throwable.printStackTrace();
      }
    }
  }

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

    @Override
    public void accept(AtomixQueryResponse atomixQueryResponse, Throwable throwable) {
      System.out.println("RECEIVED QUERY RESPONSE");
    }
  }
}
