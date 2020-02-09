package br.com.jabolina.sharder.primitive;

import br.com.jabolina.sharder.registry.NodeRegistry;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author jabolina
 * @date 2/8/20
 */
public interface SharderPrimitiveFactory {
  int MAX_TIMEOUT_SECS = 30;

  // TODO: req/res
  default <K, V> void map(String primitiveName, K key, V value) {
    try {
      primitive(primitiveName, key, value).get(MAX_TIMEOUT_SECS, TimeUnit.SECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      e.printStackTrace();
    }
  }

  default <E> void queue(String primitiveName, E element) {
    try {
      primitive(primitiveName, element).get(MAX_TIMEOUT_SECS, TimeUnit.SECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      e.printStackTrace();
    }
  }

  <K, V> void execute(String primitiveName, K key, BiFunction<K, Collection<V>, Collection<V>> func);

  <E> void execute(String primitiveName, Function<Collection<E>, Collection<E>> func);

  <K, V> CompletableFuture<Void> primitive(String primitiveName, K key, V value);

  <E> CompletableFuture<Void> primitive(String primitiveName, E element);

  interface Builder<T extends SharderPrimitiveFactory, U extends Builder> extends br.com.jabolina.sharder.utils.contract.Builder<T> {

    /**
     * Define the node registry that will be used.
     *
     * @param nodeRegistry: registry for nodes
     * @return Primitive client builder
     */
    U withNodeRegistry(NodeRegistry nodeRegistry);
  }
}
