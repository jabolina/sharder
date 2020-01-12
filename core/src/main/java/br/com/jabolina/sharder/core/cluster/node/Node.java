package br.com.jabolina.sharder.core.cluster.node;

import br.com.jabolina.sharder.core.cluster.Cluster;
import br.com.jabolina.sharder.core.cluster.Member;
import br.com.jabolina.sharder.core.utils.contract.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Node member of the cluster, this is the most basic element inside a cluster.
 * </p>
 * Each {@code Cluster} will have at least one node within, the communication inner-cluster
 * will be with node <--> node. Each node will hold pieces of data and will execute requests
 * only if it is the owner of the data, otherwise the node will proxy the data through the
 * {@code Registry} to the correct owner.
 *
 * A node can be created using the builder pattern, like:
 * <pre>
 *   {@code
 *   Node node = Node.builder()
 *       .withNodeName("node-1")
 *       .withAddress("127.0.0.1")
 *       .withPort(5000)
 *       .build()
 *   }
 * </pre>
 *
 * @author jab
 * @date 1/11/20
 */
public class Node implements Component<Node>, Member {
  private final NodeConfiguration configuration;
  private final AtomicBoolean running = new AtomicBoolean(false);

  Node(NodeConfiguration configuration) {
    this.configuration = configuration;
  }

  public static NodeBuilder builder() {
    return builder(new NodeConfiguration());
  }

  public static NodeBuilder builder(NodeConfiguration configuration) {
    return new NodeBuilder(configuration);
  }

  @Override
  @SuppressWarnings("unchecked")
  public CompletableFuture<Node> start() {
    CompletableFuture<Node> future = new CompletableFuture<>();
    if (running.compareAndSet(false, true)) {
      configuration.getCluster().registry()
          .register(this)
          .thenRun(() -> future.complete(this));
    }
    return future.thenApply(v -> this);
  }

  @Override
  @SuppressWarnings("unchecked")
  public CompletableFuture<Void> stop() {
    return configuration.getCluster().registry()
        .unregister(this);
  }

  @Override
  public boolean isRunning() {
    return running.get();
  }

  @Override
  public String getName() {
    return configuration.getNodeName();
  }

  @Override
  public void ehlo(Member member) {
    configuration.setCluster((Cluster) member);
  }
}
