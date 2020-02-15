package br.com.jabolina.sharder.atomix;

import br.com.jabolina.sharder.message.atomix.operation.ExecuteOperation;
import br.com.jabolina.sharder.message.atomix.request.AtomixExecuteRequest;
import br.com.jabolina.sharder.message.atomix.response.AtomixExecuteResponse;
import br.com.jabolina.sharder.utils.contract.Conversor;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * @author jabolina
 * @date 2/15/20
 */
public class AtomixExecuteHandler implements Function<AtomixExecuteRequest, CompletableFuture<AtomixExecuteResponse>> {
  private final AtomixWrapper atomix;
  private final Conversor<AtomixExecuteRequest, CompletableFuture<AtomixExecuteResponse>> EXECUTE_CONVERSOR = request -> {
    CompletableFuture<AtomixExecuteResponse> future = new CompletableFuture<>();
    ExecuteOperation operation = request.operation();
    AtomixExecuteHandler.this.atomix.execute(atx -> {
      AtomixExecuteResponse response = AtomixExecuteResponse.builder()
          .withResult(new byte[0])
          .build();
      future.complete(response);
      return response;
    });
    return future;
  };

  public AtomixExecuteHandler(AtomixWrapper atomix) {
    this.atomix = atomix;
  }

  @Override
  public CompletableFuture<AtomixExecuteResponse> apply(AtomixExecuteRequest request) {
    return EXECUTE_CONVERSOR.convert(request);
  }
}
