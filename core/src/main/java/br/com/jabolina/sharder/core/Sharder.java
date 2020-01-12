package br.com.jabolina.sharder.core;

import br.com.jabolina.sharder.core.cluster.Cluster;
import br.com.jabolina.sharder.core.concurrent.ConcurrentContext;
import br.com.jabolina.sharder.core.concurrent.ConcurrentNamingFactory;
import br.com.jabolina.sharder.core.concurrent.SingleConcurrent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Entry point to manipulate and connect to a sharding cluster, create components and node configuration.
 *
 * @author jab
 * @date 1/11/20
 */
public class Sharder extends Cluster {
  private static final Logger LOGGER = LoggerFactory.getLogger(Sharder.class);
  private final Cluster cluster;
  private final SharderConfiguration sharderConfiguration;
  private final ScheduledExecutorService scheduledExecutor;
  private final ConcurrentContext context = new SingleConcurrent("sharder-%d");

  public static SharderBuilder builder() {
    return new SharderBuilder();
  }

  protected Sharder(SharderConfiguration clusterConfiguration, Cluster cluster) {
    super(clusterConfiguration.getClusterConfiguration());
    this.cluster = cluster;
    this.sharderConfiguration = clusterConfiguration;
    this.scheduledExecutor = Executors.newScheduledThreadPool(
        Math.max(Math.min(Runtime.getRuntime().availableProcessors() * 2, 8), 4),
        ConcurrentNamingFactory.name("sharder-c-%d", LOGGER)
    );
  }

  @Override
  public CompletableFuture<Cluster> start() {
    return super.start().thenApply(ignore -> {
      if (sharderConfiguration.isShutdownHook()) {
        Thread hook = sharderConfiguration.getOnShutdown();
        if (hook == null) {
          hook = new Thread(() -> super.stop().join());
        }
        Runtime.getRuntime().addShutdownHook(hook);
      }

      return this;
    });
  }

  @Override
  public CompletableFuture<Void> stop() {
    if (sharderConfiguration.getOnShutdown() != null) {
      try {
        Runtime.getRuntime().removeShutdownHook(sharderConfiguration.getOnShutdown());
        sharderConfiguration.setOnShutdown(null);
      } catch (IllegalStateException ignore) { }
    }
    scheduledExecutor.shutdownNow();
    context.close();
    return super.stop();
  }
}
