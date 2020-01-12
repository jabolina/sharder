package br.com.jabolina.sharder.core.cluster;

import br.com.jabolina.sharder.core.cluster.node.Node;
import br.com.jabolina.sharder.core.communication.multicast.MulticastComponent;
import br.com.jabolina.sharder.core.registry.NodeRegistry;
import br.com.jabolina.sharder.core.registry.Registry;
import br.com.jabolina.sharder.core.utils.contract.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Configuration for the cluster
 *
 * @author jab
 * @date 1/11/20
 */
public class ClusterConfiguration implements Configuration {
  private static final String CLUSTER_PREFIX = "cshard";

  private String clusterName = String.format("%s-%s", CLUSTER_PREFIX, UUID.randomUUID().toString());
  private String address;
  private Integer port;
  private List<Node> nodes = Collections.emptyList();
  private MulticastComponent multicastMessaging;
  private Registry registry = NodeRegistry.builder()
      .build();

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
   * Get node address
   *
   * @return node address
   */
  public String getAddress() {
    return address;
  }

  /**
   * Set node address
   *
   * @param address: address where the node is
   * @return node
   */
  public ClusterConfiguration setAddress(String address) {
    this.address = address;
    return this;
  }

  /**
   * Get node address port
   *
   * @return node address port
   */
  public Integer getPort() {
    return port;
  }

  /**
   * Set node address port
   *
   * @param port: address port
   * @return node
   */
  public ClusterConfiguration setPort(Integer port) {
    this.port = port;
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
   * Get multicast messaging component
   *
   * @return multicast messaging component
   */
  public MulticastComponent getMulticastMessaging() {
    return multicastMessaging;
  }

  /**
   * Set multicast component to sent message through multicast
   *
   * @param multicastMessaging: multicast service
   * @return cluster configuration
   */
  public ClusterConfiguration setMulticastMessaging(MulticastComponent multicastMessaging) {
    this.multicastMessaging = multicastMessaging;
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
