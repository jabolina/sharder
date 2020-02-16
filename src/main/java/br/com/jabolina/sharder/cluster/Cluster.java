package br.com.jabolina.sharder.cluster;

import br.com.jabolina.sharder.cluster.node.Node;
import br.com.jabolina.sharder.communication.Address;
import br.com.jabolina.sharder.communication.multicast.MulticastComponent;
import br.com.jabolina.sharder.communication.multicast.NettyMulticast;
import br.com.jabolina.sharder.concurrent.ConcurrentContext;
import br.com.jabolina.sharder.concurrent.SingleConcurrent;
import br.com.jabolina.sharder.message.AbstractSharderMessageResponse;
import br.com.jabolina.sharder.primitive.Action;
import br.com.jabolina.sharder.primitive.SharderPrimitive;
import br.com.jabolina.sharder.primitive.SharderPrimitiveClient;
import br.com.jabolina.sharder.primitive.SharderPrimitiveFactory;
import br.com.jabolina.sharder.registry.NodeRegistry;
import br.com.jabolina.sharder.registry.Registry;
import br.com.jabolina.sharder.utils.contract.Component;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Cluster manager
 *
 * @author jab
 * @date 1/11/20
 */
public class Cluster implements Component<Cluster>, Member, SharderPrimitiveFactory {
  private final ClusterConfiguration clusterConfiguration;
  private final ConcurrentContext context = new SingleConcurrent("cluster-%d");
  private final AtomicBoolean running = new AtomicBoolean(false);
  private final NodeRegistry registry;
  private final SharderPrimitive primitiveClient;
  private CompletableFuture<Cluster> starting;
  private CompletableFuture<Void> stopping;

  public Cluster(ClusterConfiguration clusterConfiguration) {
    this.clusterConfiguration = clusterConfiguration;
    this.registry = registryComponent(clusterConfiguration);
    clusterConfiguration.getNodes().forEach(node -> node.ehlo(this));
    this.primitiveClient = SharderPrimitiveClient.builder()
        .withNodeRegistry(this.registry)
        .build();
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
        .thenComposeAsync(ignore -> registry().start(), context)
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
    return registry;
  }

  private NodeRegistry registryComponent(ClusterConfiguration configuration) {
    return NodeRegistry.builder()
        .withClusterConfiguration(configuration)
        .withMulticastMessaging(multicastComponent(configuration))
        .build();
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

  public ClusterConfiguration configuration() {
    return clusterConfiguration;
  }

  @Override
  public <K, V> void execute(String primitiveName, K key, BiFunction<K, Collection<V>, Collection<V>> func) {
    primitiveClient.execute(primitiveName, key, func);
  }

  @Override
  public <E> void execute(String primitiveName, Function<Collection<E>, Collection<E>> func) {
    primitiveClient.execute(primitiveName, func);
  }

  @Override
  public <K, V> CompletableFuture<AbstractSharderMessageResponse> primitive(String primitiveName, K key, V value, Action action) {
    return primitiveClient.primitive(primitiveName, key, value, action);
  }

  @Override
  public <E> CompletableFuture<AbstractSharderMessageResponse> primitive(String primitiveName, E element, Action action) {
    return primitiveClient.primitive(primitiveName, element, action);
  }
}
