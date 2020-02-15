package br.com.jabolina.sharder.atomix;

import br.com.jabolina.sharder.concurrent.ConcurrentContext;
import br.com.jabolina.sharder.exception.Error;
import br.com.jabolina.sharder.message.AbstractSharderMessageRequest;
import br.com.jabolina.sharder.message.AbstractSharderMessageResponse;
import br.com.jabolina.sharder.message.atomix.request.AtomixExecuteRequest;
import br.com.jabolina.sharder.message.atomix.request.AtomixQueryRequest;
import br.com.jabolina.sharder.message.atomix.response.AtomixExecuteResponse;
import br.com.jabolina.sharder.message.atomix.response.AtomixQueryResponse;

import java.net.ConnectException;
import java.net.SocketException;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author jabolina
 * @date 2/15/20
 */
final class AtomixSessionConnection {
  private static final int REQUEST_MAX_ATTEMPTS = 50;
  private static final Predicate<AbstractSharderMessageResponse> COMPLETE_PREDICATE = res ->
      AbstractSharderMessageResponse.Status.OK.equals(res.status())
          || Error.FAILURE.equals(res.error().error())
          || Error.UNAVAILABLE.equals(res.error().error())
          || Error.NOT_READY.equals(res.error().error())
          || Error.UNKNOWN.equals(res.error().error());
  private final AtomixWrapper atomix;
  private final ConcurrentContext context;

  AtomixSessionConnection(AtomixWrapper atomix, ConcurrentContext context) {
    this.atomix = atomix;
    this.context = context;
  }

  public CompletableFuture<AtomixExecuteResponse> execute(AtomixExecuteRequest request) {
    return invoke(request, new AtomixExecuteHandler(atomix));
  }

  public CompletableFuture<AtomixQueryResponse> query(AtomixQueryRequest request) {
    return invoke(request, new AtomixQueryHandler(atomix));
  }

  private <T extends AbstractSharderMessageRequest, U extends AbstractSharderMessageResponse> CompletableFuture<U> invoke(
      T request,
      Function<T, CompletableFuture<U>> execute) {
    CompletableFuture<U> future = new CompletableFuture<>();
    if (this.context.isCurrentContext()) {
      invoke(request, execute, future);
    } else {
      context.execute(() -> invoke(request, execute, future));
    }

    return future;
  }

  private <T extends AbstractSharderMessageRequest, U extends AbstractSharderMessageResponse> void invoke(
      T request,
      Function<T, CompletableFuture<U>> execute,
      CompletableFuture<U> future) {
    invoke(request, execute, future, 0);
  }

  private <T extends AbstractSharderMessageRequest, U extends AbstractSharderMessageResponse> void invoke(
      T request,
      Function<T, CompletableFuture<U>> execute,
      CompletableFuture<U> future,
      int attempt) {
    if (REQUEST_MAX_ATTEMPTS < attempt) {
      execute.apply(request).whenCompleteAsync((res, err) -> {
        if (err != null || res != null) {
          response(request, res, err, execute, future, attempt);
        } else {
          // should not happen
          future.complete(null);
        }
      });
    } else {
      future.completeExceptionally(new ConnectException("Could not connect to Atomix"));
    }
  }

  private <T extends AbstractSharderMessageRequest, U extends AbstractSharderMessageResponse> void response(
      T req,
      U res,
      Throwable error,
      Function<T, CompletableFuture<U>> execute,
      CompletableFuture<U> future,
      int attempt) {
    if (error == null) {
      if (COMPLETE_PREDICATE.test(res)) {
        future.complete(res);
      } else {
        invoke(req, execute, future, attempt + 1);
      }
    } else {
      if (error instanceof CompletionException) {
        error = error.getCause();
      }

      if (error instanceof SocketException || error instanceof TimeoutException || error instanceof ClosedChannelException) {
        if (attempt < REQUEST_MAX_ATTEMPTS) {
          invoke(req, execute, future, attempt + 1);
        } else {
          future.completeExceptionally(error);
        }
      } else {
        future.completeExceptionally(error);
      }
    }
  }
}
