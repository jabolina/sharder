package br.com.jabolina.sharder.atomix;

import br.com.jabolina.sharder.cluster.ClusterConfiguration;
import br.com.jabolina.sharder.cluster.node.NodeConfiguration;
import io.atomix.core.Atomix;
import io.atomix.utils.logging.ContextualLoggerFactory;
import io.atomix.utils.logging.LoggerContext;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * Wrapper around Atomix, all requests to Atomix will be issued through this wrapper.
 *
 * @author jabolina
 * @date 2/1/20
 */
public class AtomixWrapper implements Wrapper {
  private final Logger log;
  private final int id;
  private final Atomix atomix;
  private final AtomicBoolean running = new AtomicBoolean(false);

  public AtomixWrapper(int id, ClusterConfiguration clusterConfiguration, NodeConfiguration nodeConfiguration) {
    this.id = id;
    this.atomix = atomix(clusterConfiguration, nodeConfiguration);
    this.log = ContextualLoggerFactory.getLogger(getClass(), LoggerContext.builder(getClass())
        .addValue(clusterConfiguration.getClusterName())
        .build());
  }

  @Override
  public int id() {
    return id;
  }

  @Override
  public CompletableFuture<AtomixWrapper> start() {
    if (running.compareAndSet(false, true)) {
      log.debug("Starting atomix wrapper");
      return atomix.start()
          .thenApply(v -> this);
    }

    return CompletableFuture.completedFuture(this);
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

  /**
   * Execute function using Atomix
   *
   * @param function: Function to be executed with Atomix
   * @param <T>: type of the Atomix response
   * @return response of type T
   */
  public <T> T execute(Function<Atomix, T> function) {
    return function.apply(atomix);
  }
}
