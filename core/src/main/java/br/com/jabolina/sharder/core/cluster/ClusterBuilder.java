package br.com.jabolina.sharder.core.cluster;

import br.com.jabolina.sharder.core.cluster.node.Node;
import br.com.jabolina.sharder.core.registry.Registry;
import br.com.jabolina.sharder.core.utils.contract.Builder;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author jab
 * @date 1/11/20
 */
public class ClusterBuilder implements Builder<Cluster> {
  protected final ClusterConfiguration clusterConfiguration;

  public ClusterBuilder() {
    this.clusterConfiguration = new ClusterConfiguration();
  }

  public ClusterBuilder withClusterName(String name) {
    clusterConfiguration.setClusterName(Objects.requireNonNull(name, "Cluster name cannot be null!"));
    return this;
  }

  public ClusterBuilder withRegistry(Registry registry) {
    clusterConfiguration.setRegistry(Objects.requireNonNull(registry, "Registry cannot be null!"));
    return this;
  }

  public ClusterBuilder withAddress(String address) {
    clusterConfiguration.setAddress(Objects.requireNonNull(address, "Node address cannot be null!"));
    return this;
  }

  public ClusterBuilder withPort(Integer port) {
    clusterConfiguration.setPort(Objects.requireNonNull(port, "Node address port cannot be null!"));
    return this;
  }

  public ClusterBuilder withNode(Node node) {
    return withNodes(node);
  }

  public ClusterBuilder withNodes(Node ... nodes) {
    clusterConfiguration.setNodes(Arrays.asList(nodes));
    return this;
  }

  @Override
  public Cluster build() {
    Cluster cluster = new Cluster(clusterConfiguration, null);
    clusterConfiguration.getNodes().forEach(node -> node.ehlo(cluster));
    return cluster;
  }
}
