package br.com.jabolina.sharder.atomix;

import br.com.jabolina.sharder.message.atomix.operation.QueryOperation;
import br.com.jabolina.sharder.message.atomix.request.AtomixQueryRequest;
import br.com.jabolina.sharder.message.atomix.response.AtomixQueryResponse;
import br.com.jabolina.sharder.utils.contract.Converter;

import java.util.concurrent.CompletableFuture;

/**
 * @author jabolina
 * @date 2/15/20
 */
public final class AtomixQueryHandler extends AtomixRequestHandler<AtomixQueryRequest, CompletableFuture<AtomixQueryResponse>> {
  private final Converter<AtomixQueryRequest, CompletableFuture<AtomixQueryResponse>> converter = request -> {
    CompletableFuture<AtomixQueryResponse> future = new CompletableFuture<>();
    QueryOperation operation = request.operation();
    AtomixQueryHandler.this.wrapper.execute(atomix -> {
      AtomixQueryResponse response = AtomixQueryResponse.builder()
          .withResult(new byte[0])
          .build();
      future.complete(response);
      return response;
    });
    return future;
  };

  AtomixQueryHandler(AtomixWrapper wrapper) {
    super(wrapper);
  }

  @Override
  protected Converter<AtomixQueryRequest, CompletableFuture<AtomixQueryResponse>> converter() {
    return converter;
  }
}
