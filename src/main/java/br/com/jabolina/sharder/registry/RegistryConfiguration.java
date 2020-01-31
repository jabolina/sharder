package br.com.jabolina.sharder.registry;

import br.com.jabolina.sharder.communication.multicast.MulticastComponent;
import br.com.jabolina.sharder.cluster.ClusterConfiguration;
import br.com.jabolina.sharder.utils.contract.Configuration;

/**
 * Basic configuration for any created registry
 *
 * @author jab
 * @date 1/11/20
 */
public class RegistryConfiguration implements Configuration {
  private ClusterConfiguration clusterConfiguration;
  private MulticastComponent multicastComponent;

  public ClusterConfiguration getClusterConfiguration() {
    return clusterConfiguration;
  }

  public RegistryConfiguration setClusterConfiguration(ClusterConfiguration clusterConfiguration) {
    this.clusterConfiguration = clusterConfiguration;
    return this;
  }

  public MulticastComponent getMulticastComponent() {
    return multicastComponent;
  }

  public RegistryConfiguration setMulticastComponent(MulticastComponent multicastComponent) {
    this.multicastComponent = multicastComponent;
    return this;
  }
}
