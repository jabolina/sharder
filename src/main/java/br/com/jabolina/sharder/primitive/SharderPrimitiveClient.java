package br.com.jabolina.sharder.primitive;

import br.com.jabolina.sharder.communication.multicast.Multicast;
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
  private final NodeRegistry nodeRegistry;
  private final Multicast multicast;

  public SharderPrimitiveClient(NodeRegistry nodeRegistry) {
    this.nodeRegistry = nodeRegistry;
    this.multicast = nodeRegistry.getRegistryConfiguration().getMulticastComponent();
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
    return primitiveRegistry.register(new PrimitiveHolder(primitiveName, value.getClass().getTypeName()))
        .thenApply(v -> {
          log.debug("Multicast map primitive");
          MapPrimitive<K, V> primitive = new MapPrimitive<>(primitiveName, key, value);
          nodeRegistry.members().forEach(node -> multicast.subscribe(primitiveName, bytes -> {
            MapPrimitive<K, V> received = primitive.serializer().decode(bytes);
            log.debug("Received for key [{}] value [{}]", received.key(), received.value());
          }));
          multicast.multicast(primitiveName, primitive.serialize());
          return v;
        })
        .thenApply(v -> null);
  }

  @Override
  public <E> CompletableFuture<Void> primitive(String primitiveName, E element) {
    return primitiveRegistry.register(new PrimitiveHolder(primitiveName, element.getClass().getTypeName()))
        .thenApply(v -> {
          CollectionPrimitive<E> primitive = new CollectionPrimitive<>(primitiveName, element);
          nodeRegistry.members().forEach(node -> multicast.subscribe(primitiveName, bytes -> {
            CollectionPrimitive<E> received = primitive.serializer().decode(bytes);
            log.debug("Received on collection [{}]", received.element());
          }));
          multicast.multicast(primitiveName, primitive.serialize());
          return v;
        })
        .thenApply(v -> null);
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
