package br.com.jabolina.sharder.cluster.node;

import br.com.jabolina.sharder.cluster.Cluster;
import br.com.jabolina.sharder.utils.contract.Configuration;
import io.atomix.utils.net.Address;

import java.util.UUID;

/**
 * @author jab
 * @date 1/11/20
 */
public class NodeConfiguration implements Configuration {
  private static final String NODE_PREFIX = "nshard";

  private String nodeName = String.format("%s-%s", NODE_PREFIX, UUID.randomUUID().toString());
  private Cluster cluster;
  private Address atomixClusterAddress;
  private Address atomixNodeAddress;

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

  public Address atomixClusterAddress() {
    return atomixClusterAddress;
  }

  public NodeConfiguration setAtomixClusterAddress(Address atomixClusterAddress) {
    this.atomixClusterAddress = atomixClusterAddress;
    return this;
  }

  public Address atomixNodeAddress() {
    return atomixNodeAddress;
  }

  public NodeConfiguration setAtomixNodeAddress(Address atomixNodeAddress) {
    this.atomixNodeAddress = atomixNodeAddress;
    return this;
  }
}
