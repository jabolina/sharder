package br.com.jabolina.sharder.core.cluster.node;

import br.com.jabolina.sharder.core.cluster.Member;
import br.com.jabolina.sharder.core.utils.contract.StatefulComponent;
import br.com.jabolina.sharder.core.utils.enumeration.ComponentStatus;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author jab
 * @date 1/11/20
 */
public class Node implements StatefulComponent<Node>, Member {
  private final NodeConfiguration configuration;
  private final AtomicBoolean running = new AtomicBoolean(false);
  private ComponentStatus status = ComponentStatus.NOT_STARTED;

  public Node(NodeConfiguration configuration) {
    this.configuration = configuration;
  }

  public static NodeBuilder builder() {
    return builder(new NodeConfiguration());
  }

  public static NodeBuilder builder(NodeConfiguration configuration) {
    return new NodeBuilder(configuration);
  }

  @Override
  public CompletableFuture<Node> start() {
    updateStatus(ComponentStatus.STARTING);
    return null;
  }

  @Override
  public CompletableFuture<Void> stop() {
    updateStatus(ComponentStatus.STOPPING);
    return null;
  }

  @Override
  public boolean isRunning() {
    if (!running.get() || status.equals(ComponentStatus.STARTING) || status.equals(ComponentStatus.STOPPING)) {
      return false;
    }

    return running.get();
  }

  @Override
  public Node updateStatus(ComponentStatus updated) {
    this.status = updated;
    return this;
  }

  @Override
  public ComponentStatus currentStatus() {
    return status;
  }

  @Override
  public String getName() {
    return configuration.getNodeName();
  }
}
