package br.com.jabolina.sharder.primitive;

import br.com.jabolina.sharder.registry.NodeRegistry;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author jabolina
 * @date 2/8/20
 */
public interface SharderPrimitiveFactory {
  int MAX_TIMEOUT_SECS = 30;

  default <K, V> CompletableFuture<Void> primitive(String primitiveName, K key, V value) {
    return primitive(primitiveName, key, value, Action.READ);
  }

  default <T> CompletableFuture<Void> primitive(String primitiveName, T element) {
    return primitive(primitiveName, element, Action.READ);
  }

  <K, V> void execute(String primitiveName, K key, BiFunction<K, Collection<V>, Collection<V>> func);

  <E> void execute(String primitiveName, Function<Collection<E>, Collection<E>> func);

  <K, V> CompletableFuture<Void> primitive(String primitiveName, K key, V value, Action action);

  <T> CompletableFuture<Void> primitive(String primitiveName, T element, Action action);

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
