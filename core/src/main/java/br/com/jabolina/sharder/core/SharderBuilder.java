package br.com.jabolina.sharder.core;

import br.com.jabolina.sharder.core.cluster.ClusterBuilder;
import br.com.jabolina.sharder.core.cluster.node.Node;
import br.com.jabolina.sharder.core.registry.Registry;

import java.util.Objects;

/**
 * Sharder instance builder.
 *
 * @author jab
 * @date 1/11/20
 */
public class SharderBuilder extends ClusterBuilder {
  private final SharderConfiguration sharderConfiguration;
  private final Registry registry;

  public SharderBuilder(SharderConfiguration sharderConfiguration, Registry registry) {
    super(sharderConfiguration.getClusterConfiguration());
    this.sharderConfiguration = Objects.requireNonNull(sharderConfiguration, "Sharder configuration cannot be null!");
    this.registry = Objects.requireNonNull(registry, "Sharder instance registry cannot be null!");
  }

  public SharderBuilder withShutdownHook(boolean value) {
    sharderConfiguration.setShutdownHook(value);
    return this;
  }

  @Override
  public ClusterBuilder withClusterName(String name) {
    super.withClusterName(name);
    return this;
  }

  @Override
  public ClusterBuilder withNode(Node node) {
    super.withNode(node);
    return this;
  }

  @Override
  public ClusterBuilder withNodes(Node... nodes) {
    super.withNodes(nodes);
    return this;
  }

  @Override
  public ClusterBuilder withRegistry(Registry registry) {
    super.withRegistry(registry);
    return this;
  }

  @Override
  public Sharder build() {
    return new Sharder(sharderConfiguration);
  }
}
