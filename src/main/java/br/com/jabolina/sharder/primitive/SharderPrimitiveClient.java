package br.com.jabolina.sharder.primitive;

import br.com.jabolina.sharder.registry.NodeRegistry;
import br.com.jabolina.sharder.registry.PrimitiveRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
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
  private static final Logger log = LoggerFactory.getLogger(SharderPrimitiveClient.class);
  private final AtomicBoolean started = new AtomicBoolean(false);
  private final PrimitiveRegistry primitiveRegistry;
  private final NodeRegistry nodeRegistry;

  public SharderPrimitiveClient(NodeRegistry nodeRegistry) {
    this.nodeRegistry = nodeRegistry;
    this.primitiveRegistry = PrimitiveRegistry.builder()
        .build();
  }

  @Override
  public <K, V> void execute(String primitiveName, K key, BiFunction<K, Collection<V>, Collection<V>> func) {

  }

  @Override
  public <E> void execute(String primitiveName, Function<Collection<E>, Collection<E>> func) {

  }

  @Override
  public <K, V> CompletableFuture<Void> primitive(String primitiveName, K key, V value) {
    return null;
  }

  @Override
  public <E> CompletableFuture<Void> primitive(String primitiveName, E element) {
    return null;
  }

  @Override
  public CompletableFuture<SharderPrimitive> start() {
    return primitiveRegistry.start().thenApply(v -> this);
  }

  @Override
  public CompletableFuture<Void> stop() {
    return primitiveRegistry.stop();
  }

  @Override
  public boolean isRunning() {
    return primitiveRegistry.isRunning();
  }
}
