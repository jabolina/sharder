package br.com.jabolina.sharder.core.cluster;

import br.com.jabolina.sharder.core.registry.Registry;
import br.com.jabolina.sharder.core.utils.contract.Configuration;

import java.util.UUID;

/**
 * @author jab
 * @date 1/11/20
 */
public class ClusterConfiguration implements Configuration {
  private static final String CLUSTER_PREFIX = "cshard";

  private String clusterName = String.format("%s-%s", CLUSTER_PREFIX, UUID.randomUUID().toString());
  private Registry clusterRegistry;
  // registry configuration


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
   * Retrieve shard registry
   *
   * @return sharder registry
   */
  public Registry getClusterRegistry() {
    return clusterRegistry;
  }

  /**
   * Set sharder registry
   *
   * @param clusterRegistry: registry for cluster
   * @return cluster configuration
   */
  public ClusterConfiguration setClusterRegistry(Registry clusterRegistry) {
    this.clusterRegistry = clusterRegistry;
    return this;
  }
}
