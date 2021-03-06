package br.com.jabolina.sharder;

import br.com.jabolina.sharder.cluster.ClusterBuilder;
import br.com.jabolina.sharder.cluster.node.Node;
import br.com.jabolina.sharder.registry.Registry;

/**
 * Sharder instance builder.
 *
 * @author jab
 * @date 1/11/20
 */
public class SharderBuilder extends ClusterBuilder {
  private final SharderConfiguration sharderConfiguration;

  SharderBuilder() {
    super();
    this.sharderConfiguration = new SharderConfiguration(clusterConfiguration);
  }

  public SharderBuilder withShutdownHook(boolean value) {
    sharderConfiguration.setShutdownHook(value);
    return this;
  }

  @Override
  public SharderBuilder withReplication(int replication) {
    super.withReplication(replication);
    return this;
  }

  @Override
  public SharderBuilder withClusterName(String name) {
    super.withClusterName(name);
    return this;
  }

  @Override
  public SharderBuilder withNode(Node node) {
    super.withNode(node);
    return this;
  }

  @Override
  public SharderBuilder withNodes(Node... nodes) {
    super.withNodes(nodes);
    return this;
  }

  @Override
  public SharderBuilder withAddress(String address) {
    super.withAddress(address);
    return this;
  }

  @Override
  public SharderBuilder withPort(Integer port) {
    super.withPort(port);
    return this;
  }

  @Override
  public SharderBuilder withRegistry(Registry registry) {
    super.withRegistry(registry);
    return this;
  }

  @Override
  public Sharder build() {
    return new Sharder(sharderConfiguration);
  }
}
