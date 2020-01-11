package br.com.jabolina.sharder.core.registry;

import br.com.jabolina.sharder.core.cluster.node.Node;
import br.com.jabolina.sharder.core.concurrent.ConcurrentContext;
import br.com.jabolina.sharder.core.concurrent.SingleConcurrent;
import com.google.common.collect.Maps;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Node registry manager
 * </p>
 * Every node will register itself here, so all communication executed between the nodes
 * is issue through the registry.
 *
 * @author jab
 * @date 1/11/20
 */
public class NodeRegistry implements Registry<Node> {
  private static final String REGISTRY_THREAD_NAME = "nregistry-%d";
  private final ConcurrentContext context;
  private final RegistryConfiguration registryConfiguration;
  private final AtomicBoolean started;
  private final Map<String, Node> storage;

  private NodeRegistry(RegistryConfiguration configuration) {
    this.context = new SingleConcurrent(REGISTRY_THREAD_NAME);
    this.storage = Maps.newConcurrentMap();
    this.started = new AtomicBoolean(false);
    this.registryConfiguration = configuration;
  }

  /**
   * Get the registry builder, the default registry is the node registry
   *
   * @return a node registry builder
   */
  public static Builder builder() {
    return new Builder();
  }

  @Override
  public Collection<Node> members() {
    return storage.values();
  }

  @Override
  public CompletableFuture<Node> register(Node node) {
    String key = new String(node.hashName(), StandardCharsets.UTF_8);
    return CompletableFuture.completedFuture(storage.computeIfAbsent(key, k -> {
      storage.put(k, node);
      // TODO: introduce to another nodes?
      return node;
    }));
  }

  @Override
  public CompletableFuture<Void> unregister(Node node) {
    String key = new String(node.hashName(), StandardCharsets.UTF_8);
    return CompletableFuture.runAsync(() -> storage.computeIfPresent(key, (k, v) -> {
      storage.remove(k);
      // TODO: good bye to another nodes?
      return v;
    }), context);
  }

  @Override
  public CompletableFuture<Registry<Node>> start() {
    CompletableFuture<Registry<Node>> future = new CompletableFuture<>();
    if (started.compareAndSet(false, true)) {
      // TODO: start communication services inside configuration
    }

    return future.thenApply(v -> this);
  }

  @Override
  public CompletableFuture<Void> stop() {
    CompletableFuture<Void> future = new CompletableFuture<>();
    if (started.compareAndSet(true, false)) {
      // TODO: stop communication services inside configuration
    }
    return future;
  }

  @Override
  public boolean isRunning() {
    return started.get();
  }

  /**
   * Build a new registry for nodes
   */
  public static class Builder extends Registry.Builder<Node, NodeRegistry> {

    @Override
    public NodeRegistry build() {
      return new NodeRegistry(registryConfiguration);
    }
  }
}
