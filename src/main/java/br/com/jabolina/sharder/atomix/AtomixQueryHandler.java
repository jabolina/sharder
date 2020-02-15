package br.com.jabolina.sharder.atomix;

import br.com.jabolina.sharder.message.atomix.operation.QueryOperation;
import br.com.jabolina.sharder.message.atomix.request.AtomixQueryRequest;
import br.com.jabolina.sharder.message.atomix.response.AtomixQueryResponse;
import br.com.jabolina.sharder.utils.contract.Conversor;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * @author jabolina
 * @date 2/15/20
 */
public class AtomixQueryHandler implements Function<AtomixQueryRequest, CompletableFuture<AtomixQueryResponse>> {
  private final AtomixWrapper wrapper;
  private final Conversor<AtomixQueryRequest, CompletableFuture<AtomixQueryResponse>> QUERY_CONVERSOR = request -> {
    CompletableFuture<AtomixQueryResponse> future = new CompletableFuture<>();
    QueryOperation operation = request.operation();
    AtomixQueryHandler.this.wrapper.execute(atx -> {
      AtomixQueryResponse response = AtomixQueryResponse.builder()
          .withResult(new byte[0])
          .build();
      future.complete(response);
      return response;
    });
    return future;
  };

  public AtomixQueryHandler(AtomixWrapper wrapper) {
    this.wrapper = wrapper;
  }

  @Override
  public CompletableFuture<AtomixQueryResponse> apply(AtomixQueryRequest request) {
    return QUERY_CONVERSOR.convert(request);
  }
}
