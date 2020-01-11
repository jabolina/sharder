package br.com.jabolina.sharder.core.cluster;

import br.com.jabolina.sharder.core.registry.Registry;
import br.com.jabolina.sharder.core.utils.contract.Builder;

import java.util.Objects;

/**
 * @author jab
 * @date 1/11/20
 */
public class ClusterBuilder implements Builder<Cluster> {
  private ClusterConfiguration clusterConfiguration;

  protected ClusterBuilder() {
    this(new ClusterConfiguration());
  }

  protected ClusterBuilder(ClusterConfiguration configuration) {
    this.clusterConfiguration = Objects.requireNonNull(configuration, "Cluster configuration cannot be null!");
  }

  public ClusterBuilder withClusterName(String name) {
    clusterConfiguration.setClusterName(Objects.requireNonNull(name, "Cluster name cannot be null!"));
    return this;
  }

  public ClusterBuilder withRegistry(Registry registry) {
    clusterConfiguration.setClusterRegistry(Objects.requireNonNull(registry, "Registry cannot be null!"));
    return this;
  }

  @Override
  public Cluster build() {
    return null;
  }
}
