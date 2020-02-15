package br.com.jabolina.sharder.atomix;

import br.com.jabolina.sharder.message.atomix.operation.ExecuteOperation;
import br.com.jabolina.sharder.message.atomix.request.AtomixExecuteRequest;
import br.com.jabolina.sharder.message.atomix.response.AtomixExecuteResponse;
import br.com.jabolina.sharder.utils.contract.Converter;

import java.util.concurrent.CompletableFuture;

/**
 * @author jabolina
 * @date 2/15/20
 */
public final class AtomixExecuteHandler extends AtomixRequestHandler<AtomixExecuteRequest, CompletableFuture<AtomixExecuteResponse>> {
  private final Converter<AtomixExecuteRequest, CompletableFuture<AtomixExecuteResponse>> converter = request -> {
    CompletableFuture<AtomixExecuteResponse> future = new CompletableFuture<>();
    ExecuteOperation operation = request.operation();
    AtomixExecuteHandler.this.wrapper.execute(atomix -> {
      AtomixExecuteResponse response = AtomixExecuteResponse.builder()
          .withResult(new byte[0])
          .build();
      future.complete(response);
      return response;
    });
    return future;
  };

  AtomixExecuteHandler(AtomixWrapper wrapper) {
    super(wrapper);
  }

  @Override
  public Converter<AtomixExecuteRequest, CompletableFuture<AtomixExecuteResponse>> converter() {
    return converter;
  }
}
