package br.com.jabolina.sharder.atomix;

import br.com.jabolina.sharder.exception.Error;
import br.com.jabolina.sharder.message.SharderMessageResponse;
import br.com.jabolina.sharder.message.atomix.operation.ExecuteOperation;
import br.com.jabolina.sharder.message.atomix.request.AtomixExecuteRequest;
import br.com.jabolina.sharder.message.atomix.response.AtomixExecuteResponse;
import br.com.jabolina.sharder.primitive.data.CollectionPrimitive;
import br.com.jabolina.sharder.primitive.data.MapPrimitive;
import br.com.jabolina.sharder.utils.contract.Converter;
import io.atomix.core.Atomix;

import java.util.Collection;
import java.util.Map;
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
      if (operation.primitive() instanceof CollectionPrimitive) {
        return future.complete(handleCollection(atomix, operation));
      }

      return future.complete(handleMap(atomix, operation));
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

  private AtomixExecuteResponse handleCollection(Atomix atomix, ExecuteOperation operation) {
    CollectionPrimitive primitive = (CollectionPrimitive) operation.primitive();

    try {
      Collection collection = atomix.getQueue(primitive.primitiveName());
      boolean response = collection.add(primitive.element());
      byte[] bytes = new byte[1];
      bytes[0] = (byte) (response ? 1 : 0);
      return AtomixExecuteResponse.builder()
          .withStatus(SharderMessageResponse.Status.OK)
          .withResult(bytes)
          .build();
    } catch (UnsupportedOperationException | ClassCastException | NullPointerException | IllegalArgumentException ex) {
      return AtomixExecuteResponse.builder()
          .withStatus(SharderMessageResponse.Status.ERROR)
          .withError(Error.WRONG_USAGE, ex.getMessage())
          .build();
    } catch (IllegalStateException ex) {
      return AtomixExecuteResponse.builder()
          .withStatus(SharderMessageResponse.Status.ERROR)
          .withError(Error.UNAVAILABLE, ex.getMessage())
          .build();
    } catch (Exception ex) {
      return AtomixExecuteResponse.builder()
          .withStatus(SharderMessageResponse.Status.ERROR)
          .withError(Error.UNKNOWN, ex.getMessage())
          .build();
    }
  }

  private AtomixExecuteResponse handleMap(Atomix atomix, ExecuteOperation operation) {
    MapPrimitive primitive = (MapPrimitive) operation.primitive();

    try {
      Map map = atomix.getMap(primitive.primitiveName());
      AtomixExecuteResponse.Builder builder = AtomixExecuteResponse.builder();
      map.compute(primitive.key(), (key, value) -> {
        builder.withResult(primitive.serializer().encode(value));
        return primitive.value();
      });

      return builder.withStatus(SharderMessageResponse.Status.OK)
          .build();
    } catch (NullPointerException | ClassCastException ex) {
      return AtomixExecuteResponse.builder()
          .withStatus(SharderMessageResponse.Status.ERROR)
          .withError(Error.WRONG_USAGE, ex.getMessage())
          .build();
    } catch (UnsupportedOperationException ex) {
      return AtomixExecuteResponse.builder()
          .withStatus(SharderMessageResponse.Status.ERROR)
          .withError(Error.FAILURE, ex.getMessage())
          .build();
    } catch (Exception ex) {
      return AtomixExecuteResponse.builder()
          .withStatus(SharderMessageResponse.Status.ERROR)
          .withError(Error.UNKNOWN, ex.getMessage())
          .build();
    }
  }
}
