package br.com.jabolina.sharder.core.cluster.node;

import br.com.jabolina.sharder.core.cluster.Cluster;
import br.com.jabolina.sharder.core.utils.contract.Builder;

import java.util.Objects;

/**
 * @author jab
 * @date 1/11/20
 */
public class NodeBuilder implements Builder<Node> {
  private NodeConfiguration nodeConfiguration;

  protected NodeBuilder() {
    this(new NodeConfiguration());
  }

  protected NodeBuilder(NodeConfiguration configuration) {
    this.nodeConfiguration = Objects.requireNonNull(configuration, "Node configuration cannot be null!");
  }

  public NodeBuilder withNodeName(String name) {
    nodeConfiguration.setNodeName(Objects.requireNonNull(name, "Node name cannot be null!"));
    return this;
  }

  public NodeBuilder withAddress(String address) {
    nodeConfiguration.setAddress(Objects.requireNonNull(address, "Node address cannot be null!"));
    return this;
  }

  public NodeBuilder withPort(Integer port) {
    nodeConfiguration.setPort(Objects.requireNonNull(port, "Node address port cannot be null!"));
    return this;
  }

  public NodeBuilder withCluster(Cluster cluster) {
    nodeConfiguration.setCluster(Objects.requireNonNull(cluster, "Node cluster cannot be null!"));
    return this;
  }

  @Override
  public Node build() {
    return null;
  }
}
