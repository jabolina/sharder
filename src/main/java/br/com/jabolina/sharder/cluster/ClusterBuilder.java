package br.com.jabolina.sharder.cluster;

import br.com.jabolina.sharder.cluster.node.Node;
import br.com.jabolina.sharder.registry.Registry;
import br.com.jabolina.sharder.utils.contract.Builder;
import com.google.common.base.Preconditions;

import java.util.Arrays;
import java.util.Objects;

/**
 * Configuration to be used for the created cluster
 *
 * @author jab
 * @date 1/11/20
 */
public class ClusterBuilder implements Builder<Cluster> {
  protected final ClusterConfiguration clusterConfiguration;

  public ClusterBuilder() {
    this.clusterConfiguration = new ClusterConfiguration();
  }

  public ClusterBuilder withReplication(int replication) {
    Preconditions.checkArgument(replication > 0, "Replication factor must greater than 0!");
    clusterConfiguration.setReplication(replication);
    return this;
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
    return new Cluster(clusterConfiguration);
  }
}
