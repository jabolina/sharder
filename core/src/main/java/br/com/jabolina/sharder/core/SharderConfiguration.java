package br.com.jabolina.sharder.core;

import br.com.jabolina.sharder.core.cluster.ClusterConfiguration;
import br.com.jabolina.sharder.core.utils.contract.Configuration;

/**
 * Configure the sharder node.
 *
 * @author jab
 * @date 1/11/20
 */
public class SharderConfiguration implements Configuration {
  private ClusterConfiguration clusterConfiguration;
  private boolean shutdownHook;
  private Thread onShutdown;

  public SharderConfiguration(ClusterConfiguration clusterConfiguration) {
    this.clusterConfiguration = clusterConfiguration;
  }

  /**
   * Get cluster configuration
   *
   * @return cluster configuration
   */
  public ClusterConfiguration getClusterConfiguration() {
    return clusterConfiguration;
  }

  /**
   * Set cluster configuration
   * @param clusterConfiguration: cluster configuration for Sharder instance
   * @return sharder configuration
   */
  public SharderConfiguration setClusterConfiguration(ClusterConfiguration clusterConfiguration) {
    this.clusterConfiguration = clusterConfiguration;
    return this;
  }

  /**
   * Thread to execute on shutdown
   *
   * @return thread to execute
   */
  public Thread getOnShutdown() {
    return onShutdown;
  }

  /**
   * Set thread to execute on shutdown
   *
   * @param onShutdown callback to be executed
   * @return sharder configuration
   */
  public SharderConfiguration setOnShutdown(Thread onShutdown) {
    this.onShutdown = onShutdown;
    return this;
  }

  /**
   * Verify shutdown hook
   *
   * @return true if shutdown hook is enabled, false otherwise
   */
  public boolean isShutdownHook() {
    return shutdownHook;
  }

  /**
   * Set if shutdown hook is enabled
   *
   * @param shutdownHook: enable or disable shutdown hook
   * @return sharder configuration
   */
  public SharderConfiguration setShutdownHook(boolean shutdownHook) {
    this.shutdownHook = shutdownHook;
    return this;
  }
}
