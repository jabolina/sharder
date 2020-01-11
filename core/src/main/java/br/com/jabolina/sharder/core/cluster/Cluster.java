package br.com.jabolina.sharder.core.cluster;

import br.com.jabolina.sharder.core.cluster.node.Node;
import br.com.jabolina.sharder.core.concurrent.ConcurrentContext;
import br.com.jabolina.sharder.core.concurrent.SingleConcurrent;
import br.com.jabolina.sharder.core.utils.contract.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Cluster manager
 *
 * @author jab
 * @date 1/11/20
 */
public class Cluster implements Component<Cluster>, Member {
  private final ClusterConfiguration clusterConfiguration;
  private final ConcurrentContext context = new SingleConcurrent("cluster-%d");
  private final AtomicBoolean running = new AtomicBoolean(false);
  private CompletableFuture<Cluster> starting;
  private CompletableFuture<Void> stopping;

  public Cluster(ClusterConfiguration clusterConfiguration) {
    this.clusterConfiguration = clusterConfiguration;
  }

  public static ClusterBuilder builder() {
    return new ClusterBuilder();
  }

  @Override
  public CompletableFuture<Cluster> start() {
    if (starting == null) {
      starting = startDependencies()
          .thenComposeAsync(ignore -> finishStart(), context);
    }

    return starting;
  }

  @Override
  public CompletableFuture<Void> stop() {
    if (stopping == null) {
      stopping = stopDependencies()
          .thenComposeAsync(ignore -> {
            context.close();
            running.set(false);
            return CompletableFuture.completedFuture(null);
          });
    }

    return stopping;
  }

  @Override
  public boolean isRunning() {
    return running.get();
  }

  private CompletableFuture<Cluster> finishStart() {
    running.set(true);
    // TODO: register cluster on registry
    return CompletableFuture.completedFuture(this);
  }

  private CompletableFuture<Void> startDependencies() {
    // TODO: start registry, communication elements
    return startNodes();
  }

  private CompletableFuture<Void> startNodes() {
    return CompletableFuture.allOf(clusterConfiguration.getNodes().stream()
        .map(Node::start).toArray(CompletableFuture[]::new));
  }

  private CompletableFuture<Void> stopDependencies() {
    // TODO: stop communication, stop registry
    // thenComposeAsync with another dependencies
    return stopNodes();
  }

  private CompletableFuture<Void> stopNodes() {
    return CompletableFuture.allOf(clusterConfiguration.getNodes().stream()
        .map(Node::stop).toArray(CompletableFuture[]::new));
  }

  @Override
  public String getName() {
    return clusterConfiguration.getClusterName();
  }
}
