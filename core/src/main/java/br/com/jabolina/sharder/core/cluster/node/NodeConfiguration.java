package br.com.jabolina.sharder.core.cluster.node;

import br.com.jabolina.sharder.core.cluster.Cluster;
import br.com.jabolina.sharder.core.utils.contract.Configuration;

import java.util.UUID;

/**
 * @author jab
 * @date 1/11/20
 */
public class NodeConfiguration implements Configuration {
  private static final String NODE_PREFIX = "nshard";

  private String nodeName = String.format("%s-%s", NODE_PREFIX, UUID.randomUUID().toString());
  private String address;
  private Integer port;
  private Cluster cluster;

  /**
   * Get node name
   *
   * @return the node name
   */
  public String getNodeName() {
    return nodeName;
  }

  /**
   * Set node name
   *
   * @param nodeName: name for node
   * @return node
   */
  public NodeConfiguration setNodeName(String nodeName) {
    this.nodeName = nodeName;
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
  public NodeConfiguration setAddress(String address) {
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
  public NodeConfiguration setPort(Integer port) {
    this.port = port;
    return this;
  }

  /**
   * Get cluster where the node belongs
   *
   * @return cluster
   */
  public Cluster getCluster() {
    return cluster;
  }

  /**
   * Set which cluster the node belongs
   *
   * @param cluster: cluster where the node belongs
   * @return node
   */
  public NodeConfiguration setCluster(Cluster cluster) {
    this.cluster = cluster;
    return this;
  }
}
