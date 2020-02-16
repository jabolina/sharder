package br.com.jabolina.sharder.atomix;

import br.com.jabolina.sharder.exception.Error;
import br.com.jabolina.sharder.message.SharderMessageResponse;
import br.com.jabolina.sharder.message.atomix.operation.QueryOperation;
import br.com.jabolina.sharder.message.atomix.request.AtomixQueryRequest;
import br.com.jabolina.sharder.message.atomix.response.AtomixQueryResponse;
import br.com.jabolina.sharder.primitive.data.CollectionPrimitive;
import br.com.jabolina.sharder.primitive.data.MapPrimitive;
import br.com.jabolina.sharder.utils.contract.Converter;
import io.atomix.core.Atomix;
import io.atomix.utils.AtomixRuntimeException;

import java.util.Collection;
import java.util.Map;
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
      if (operation.primitive() instanceof CollectionPrimitive) {
        return future.complete(handleCollection(atomix, operation));
      }

      return future.complete(handleMap(atomix, operation));
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

  private AtomixQueryResponse handleCollection(Atomix atomix, QueryOperation operation) {
    CollectionPrimitive primitive = (CollectionPrimitive) operation.primitive();

    try {
      Collection collection = atomix.getQueue(primitive.primitiveName());
      return AtomixQueryResponse.builder()
          .withStatus(SharderMessageResponse.Status.OK)
          .withResult(primitive.serializer().encode(collection))
          .build();
    } catch (AtomixRuntimeException ex) {
      return AtomixQueryResponse.builder()
          .withStatus(SharderMessageResponse.Status.ERROR)
          .withError(Error.TIMEOUT, ex.getMessage())
          .build();
    } catch (Exception ex) {
      return AtomixQueryResponse.builder()
          .withStatus(SharderMessageResponse.Status.ERROR)
          .withError(Error.UNKNOWN, ex.getMessage())
          .build();
    }
  }

  private AtomixQueryResponse handleMap(Atomix atomix, QueryOperation operation) {
    MapPrimitive primitive = (MapPrimitive) operation.primitive();

    try {
      Map map = atomix.getMap(primitive.primitiveName());
      return AtomixQueryResponse.builder()
          .withStatus(SharderMessageResponse.Status.OK)
          .withResult(primitive.serializer().encode(map))
          .build();
    } catch (AtomixRuntimeException ex) {
      return AtomixQueryResponse.builder()
          .withStatus(SharderMessageResponse.Status.ERROR)
          .withError(Error.TIMEOUT, ex.getMessage())
          .build();
    } catch (Exception ex) {
      return AtomixQueryResponse.builder()
          .withStatus(SharderMessageResponse.Status.ERROR)
          .withError(Error.UNKNOWN, ex.getMessage())
          .build();
    }
  }
}
