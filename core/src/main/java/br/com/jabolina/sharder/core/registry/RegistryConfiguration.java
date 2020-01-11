package br.com.jabolina.sharder.core.registry;

import br.com.jabolina.sharder.core.cluster.ClusterConfiguration;
import br.com.jabolina.sharder.core.utils.contract.Configuration;

/**
 * Basic configuration for any created registry
 *
 * @author jab
 * @date 1/11/20
 */
public class RegistryConfiguration implements Configuration {
  private ClusterConfiguration clusterConfiguration;

  public ClusterConfiguration getClusterConfiguration() {
    return clusterConfiguration;
  }

  public RegistryConfiguration setClusterConfiguration(ClusterConfiguration clusterConfiguration) {
    this.clusterConfiguration = clusterConfiguration;
    return this;
  }

  // TODO: communication services here, thread pool here

}
