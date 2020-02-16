package br.com.jabolina.sharder.atomix;

import br.com.jabolina.sharder.cluster.node.Node;
import br.com.jabolina.sharder.exception.Error;
import br.com.jabolina.sharder.exception.SharderRuntimeException;
import br.com.jabolina.sharder.message.AbstractSharderMessageResponse;
import br.com.jabolina.sharder.message.OperationResult;
import br.com.jabolina.sharder.message.SharderMessageResponse;
import br.com.jabolina.sharder.message.atomix.AtomixMessage;
import br.com.jabolina.sharder.message.atomix.request.AtomixExecuteRequest;
import br.com.jabolina.sharder.message.atomix.request.AtomixQueryRequest;
import br.com.jabolina.sharder.message.atomix.response.AtomixExecuteResponse;
import br.com.jabolina.sharder.message.atomix.response.AtomixQueryResponse;
import io.atomix.utils.logging.ContextualLoggerFactory;
import io.atomix.utils.logging.LoggerContext;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Atomix communicator base functionality
 *
 * @author jabolina
 * @date 2/9/20
 */
public abstract class AbstractAtomixCommunicator implements Communicator {
  protected final Logger log;
  protected final AtomixWrapper atomix;

  AbstractAtomixCommunicator(Node node) {
    this.atomix = node.atomix();
    this.log = ContextualLoggerFactory.getLogger(getClass(), LoggerContext.builder(getClass())
        .addValue(node.getName())
        .build());
  }

  final <T extends AtomixMessage> T log(T t) {
    log.debug("Communicator: {}", t);
    return t;
  }

  @Override
  public CompletableFuture<AtomixExecuteResponse> execute(AtomixExecuteRequest request) {
    log(request);
    return CompletableFuture.completedFuture(AtomixExecuteResponse.builder()
        .withStatus(SharderMessageResponse.Status.ERROR)
        .withError(Error.UNAVAILABLE)
        .build());
  }

  @Override
  public CompletableFuture<AtomixQueryResponse> query(AtomixQueryRequest request) {
    log(request);
    return CompletableFuture.completedFuture(AtomixQueryResponse.builder()
        .withStatus(SharderMessageResponse.Status.ERROR)
        .withError(Error.UNAVAILABLE)
        .build());
  }

  protected <T extends AbstractSharderMessageResponse> void complete(OperationResult result,
                                                                     AbstractSharderMessageResponse.Builder<T, ?> builder,
                                                                     Throwable error,
                                                                     CompletableFuture<T> future) {
    if (result != null && result.failed()) {
      error = result.error();
    }

    if (error == null) {
      if (result == null) {
        future.complete(builder.withStatus(SharderMessageResponse.Status.ERROR)
            .withError(Error.FAILURE)
            .build());
      } else {
        future.complete(builder.withStatus(SharderMessageResponse.Status.OK)
            .withResult(result.result())
            .build());
      }
    } else if (error instanceof CompletionException && error.getCause() instanceof SharderRuntimeException) {
      future.complete(builder.withStatus(SharderMessageResponse.Status.ERROR)
          .withError(((SharderRuntimeException) error.getCause()).error())
          .build());
    } else if (error instanceof SharderRuntimeException) {
      future.complete(builder.withStatus(SharderMessageResponse.Status.ERROR)
          .withError(((SharderRuntimeException) error).error())
          .build());
    } else {
      log.error("Unexpected error", error);
      future.complete(builder.withStatus(SharderMessageResponse.Status.ERROR)
          .withError(Error.UNKNOWN)
          .build());
    }
  }
}
