package br.com.jabolina.sharder.registry;

import br.com.jabolina.sharder.primitive.PrimitiveHolder;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Holds information about created primitives
 *
 * @author jabolina
 * @date 2/8/20
 */
public class PrimitiveRegistry implements Registry<PrimitiveHolder> {
  private final Map<String, PrimitiveHolder> registered = Maps.newConcurrentMap();
  private final AtomicBoolean started = new AtomicBoolean(false);

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public Collection<PrimitiveHolder> members() {
    return registered.values();
  }

  @Override
  public CompletableFuture<PrimitiveHolder> register(PrimitiveHolder primitiveHolder) {
    CompletableFuture<PrimitiveHolder> future = new CompletableFuture<>();
    registered.compute(primitiveHolder.name(), (k, v) -> {
      future.complete(v);
      return primitiveHolder;
    });
    return future;
  }

  @Override
  public CompletableFuture<Void> unregister(PrimitiveHolder primitiveHolder) {
    CompletableFuture<Void> future = new CompletableFuture<>();
    registered.compute(primitiveHolder.name(), (k, v) -> {
      future.complete(null);
      return null;
    });
    return future;
  }

  @Override
  public CompletableFuture<Registry<PrimitiveHolder>> start() {
    return CompletableFuture.completedFuture(this);
  }

  @Override
  public CompletableFuture<Void> stop() {
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public boolean isRunning() {
    return started.get();
  }

  public static class Builder implements Registry.Builder<PrimitiveRegistry, Builder> {

    @Override
    public PrimitiveRegistry build() {
      return new PrimitiveRegistry();
    }
  }
}
