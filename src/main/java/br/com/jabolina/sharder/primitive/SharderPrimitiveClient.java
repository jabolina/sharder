package br.com.jabolina.sharder.primitive;

import br.com.jabolina.sharder.atomix.AtomixClient;
import br.com.jabolina.sharder.atomix.DefaultAtomixClient;
import br.com.jabolina.sharder.communication.multicast.Multicast;
import br.com.jabolina.sharder.concurrent.ConcurrentContext;
import br.com.jabolina.sharder.concurrent.ConcurrentPoolFactory;
import br.com.jabolina.sharder.primitive.data.AbstractPrimitive;
import br.com.jabolina.sharder.primitive.data.CollectionPrimitive;
import br.com.jabolina.sharder.primitive.data.MapPrimitive;
import br.com.jabolina.sharder.registry.NodeRegistry;
import br.com.jabolina.sharder.registry.PrimitiveRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Creates all Atomix primitives in all nodes
 *
 * @author jabolina
 * @date 2/8/20
 */
public class SharderPrimitiveClient implements SharderPrimitive {
  private final Logger log = LoggerFactory.getLogger(SharderPrimitiveClient.class);
  private final AtomicBoolean started = new AtomicBoolean(false);
  private final PrimitiveRegistry primitiveRegistry;
  private final Multicast multicast;
  private final AtomixClient atomixClient;
  private final ConcurrentContext pool;

  public SharderPrimitiveClient(NodeRegistry nodeRegistry) {
    this.multicast = nodeRegistry.getRegistryConfiguration().getMulticastComponent();
    this.atomixClient = new DefaultAtomixClient(nodeRegistry);
    this.pool = ConcurrentPoolFactory.poolContext("atomix-client", nodeRegistry.members().size(), log);
    this.primitiveRegistry = PrimitiveRegistry.builder()
        .build();
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public <K, V> void execute(String primitiveName, K key, BiFunction<K, Collection<V>, Collection<V>> func) {
  }

  @Override
  public <E> void execute(String primitiveName, Function<Collection<E>, Collection<E>> func) {

  }

  @Override
  public <K, V> CompletableFuture<Void> primitive(String primitiveName, K key, V value) {
    CompletableFuture<Void> future = new CompletableFuture<>();
    final MapPrimitive<K, V> primitive = new MapPrimitive<>(primitiveName, key, value);
    return primitiveRegistry.register(new PrimitiveHolder(primitiveName, value.getClass().getTypeName()))
        .thenApply(v -> {
          primitive(v, primitive, future);
          return v;
        })
        .thenRun(() -> multicast.multicast(primitiveName, primitive.serialize()));
  }

  @Override
  public <E> CompletableFuture<Void> primitive(String primitiveName, E element) {
    CompletableFuture<Void> future = new CompletableFuture<>();
    CollectionPrimitive<E> primitive = new CollectionPrimitive<>(primitiveName, element);
    primitiveRegistry.register(new PrimitiveHolder(primitiveName, element.getClass().getTypeName()))
        .thenApply(v -> {
          primitive(v, primitive, future);
          return v;
        })
        .thenRun(() -> multicast.multicast(primitiveName, primitive.serialize()));

    return future;
  }

  @Override
  public CompletableFuture<SharderPrimitive> start() {
    if (started.compareAndSet(false, true)) {
      return primitiveRegistry.start().thenApply(v -> this);
    }

    return CompletableFuture.completedFuture(this);
  }

  @Override
  public CompletableFuture<Void> stop() {
    if (started.compareAndSet(true, false)) {
      return primitiveRegistry.stop();
    }

    return CompletableFuture.completedFuture(null);
  }

  @Override
  public boolean isRunning() {
    return primitiveRegistry.isRunning() && started.get();
  }

  private void primitive(PrimitiveHolder holder, AbstractPrimitive primitive, CompletableFuture<Void> future) {
    atomixClient.primitive(holder, primitive)
        .thenApplyAsync(res -> {
          log.info("Response is: {}", res);
          return res;
        }, pool)
        .whenComplete((res, err) -> future.complete(null));
  }

  public static class Builder implements SharderPrimitive.Builder<SharderPrimitiveClient, Builder> {
    private NodeRegistry registry;

    @Override
    public Builder withNodeRegistry(NodeRegistry nodeRegistry) {
      this.registry = Objects.requireNonNull(nodeRegistry, "Node registry cannot be null!");
      return this;
    }

    @Override
    public SharderPrimitiveClient build() {
      return new SharderPrimitiveClient(registry);
    }
  }
}
