package br.com.jabolina.sharder.core.cluster;

import br.com.jabolina.sharder.core.cluster.node.Node;
import br.com.jabolina.sharder.core.communication.Address;
import br.com.jabolina.sharder.core.communication.multicast.MulticastComponent;
import br.com.jabolina.sharder.core.communication.multicast.NettyMulticast;
import br.com.jabolina.sharder.core.concurrent.ConcurrentContext;
import br.com.jabolina.sharder.core.concurrent.SingleConcurrent;
import br.com.jabolina.sharder.core.registry.Registry;
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
  private final MulticastComponent multicastMessaging;
  private CompletableFuture<Cluster> starting;
  private CompletableFuture<Void> stopping;

  public Cluster(ClusterConfiguration clusterConfiguration, MulticastComponent multicastMessaging) {
    this.clusterConfiguration = clusterConfiguration;
    this.multicastMessaging = multicastMessaging != null ? multicastMessaging : multicastComponent(clusterConfiguration);
    clusterConfiguration.getNodes().forEach(node -> node.ehlo(this));
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
    return CompletableFuture.completedFuture(this);
  }

  @SuppressWarnings("unchecked")
  private CompletableFuture<Void> startDependencies() {
    return startNodes()
        // .thenComposeAsync(ignore -> registry().start(), context)
        .thenComposeAsync(ignore -> multicastMessaging.start(), context)
        .thenApply(ignore -> null);
  }

  private CompletableFuture<Void> startNodes() {
    return CompletableFuture.allOf(clusterConfiguration.getNodes().stream()
        .map(Node::start).toArray(CompletableFuture[]::new));
  }

  @SuppressWarnings("unchecked")
  private CompletableFuture<Void> stopDependencies() {
    return stopNodes()
        .thenComposeAsync(ignore -> registry().stop())
        .thenComposeAsync(ignore -> multicastMessaging.stop())
        .thenApply(ignore -> null);
  }

  private CompletableFuture<Void> stopNodes() {
    return CompletableFuture.allOf(clusterConfiguration.getNodes().stream()
        .map(Node::stop).toArray(CompletableFuture[]::new));
  }

  @Override
  public String getName() {
    return clusterConfiguration.getClusterName();
  }

  public Registry registry() {
    return clusterConfiguration.getRegistry();
  }

  private MulticastComponent multicastComponent(ClusterConfiguration configuration) {
    return NettyMulticast.builder()
        .withLocalAddr(Address.from(configuration.getAddress(), configuration.getPort()))
        .withGroupAddr(Address.from(
            configuration.getMulticastConfiguration().getGroup().getHostAddress(),
            configuration.getMulticastConfiguration().getPort(),
            configuration.getMulticastConfiguration().getGroup()))
        .build();
  }
}
