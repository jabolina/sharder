package br.com.jabolina.sharder.cluster.atomix;

import br.com.jabolina.sharder.cluster.ClusterConfiguration;
import br.com.jabolina.sharder.cluster.node.NodeConfiguration;
import io.atomix.core.Atomix;
import io.atomix.utils.logging.ContextualLoggerFactory;
import io.atomix.utils.logging.LoggerContext;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author jabolina
 * @date 2/1/20
 */
public class AtomixWrapper implements Wrapper {
  private final Logger log;
  private final Atomix atomix;
  private final AtomicBoolean running = new AtomicBoolean(false);

  public AtomixWrapper(ClusterConfiguration clusterConfiguration, NodeConfiguration nodeConfiguration) {
    this.atomix = atomix(clusterConfiguration, nodeConfiguration);
    this.log = ContextualLoggerFactory.getLogger(getClass(), LoggerContext.builder(getClass())
        .addValue(clusterConfiguration.getClusterName())
        .build());
  }

  @Override
  public CompletableFuture<AtomixWrapper> start() {
    CompletableFuture<AtomixWrapper> future = new CompletableFuture<>();

    if (running.compareAndSet(false, true)) {
      log.debug("Starting atomix wrapper");
      atomix.start().thenRun(() -> {
        log.debug("Atomix started with success!");
        future.complete(this);
      });
    }

    return future;
  }

  @Override
  public CompletableFuture<Void> stop() {
    if (running.compareAndSet(true, false)) {
      return atomix.stop();
    }

    return CompletableFuture.completedFuture(null);
  }

  @Override
  public boolean isRunning() {
    return running.get();
  }
}
