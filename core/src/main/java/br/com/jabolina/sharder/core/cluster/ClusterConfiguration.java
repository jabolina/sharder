package br.com.jabolina.sharder.core.cluster;

import br.com.jabolina.sharder.core.cluster.node.Node;
import br.com.jabolina.sharder.core.registry.Registry;
import br.com.jabolina.sharder.core.utils.contract.Configuration;

import java.util.List;
import java.util.UUID;

/**
 * @author jab
 * @date 1/11/20
 */
public class ClusterConfiguration implements Configuration {
  private static final String CLUSTER_PREFIX = "cshard";

  private String clusterName = String.format("%s-%s", CLUSTER_PREFIX, UUID.randomUUID().toString());
  private List<Node> nodes;
  private Registry registry;

  /**
   * Get cluster name
   *
   * @return cluster name
   */
  public String getClusterName() {
    return clusterName;
  }

  /**
   * Set the cluster name
   *
   * @param clusterName: name for the cluster
   * @return cluster configuration
   */
  public ClusterConfiguration setClusterName(String clusterName) {
    this.clusterName = clusterName;
    return this;
  }

  /**
   * Get cluster nodes
   *
   * @return cluster nodes
   */
  public List<Node> getNodes() {
    return nodes;
  }

  /**
   * Set cluster nodes
   *
   * @param nodes: cluster nodes
   * @return cluster
   */
  public ClusterConfiguration setNodes(List<Node> nodes) {
    this.nodes = nodes;
    return this;
  }

  /**
   * Retrieve shard registry
   *
   * @return sharder registry
   */
  public Registry getRegistry() {
    return registry;
  }

  /**
   * Set sharder registry
   *
   * @param registry: registry for cluster
   * @return cluster configuration
   */
  public ClusterConfiguration setRegistry(Registry registry) {
    this.registry = registry;
    return this;
  }
}
