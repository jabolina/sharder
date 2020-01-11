package br.com.jabolina.sharder.core.utils.enumeration;

/**
 * Shard component status
 *
 * @author jab
 * @date 1/11/20
 */
public enum ComponentStatus {
  /**
   * Shard component start was never called
   */
  NOT_STARTED,

  /**
   * Shard component start was called but not started yet
   */
  STARTING,

  /**
   * Shard component is already running
   */
  RUNNING,


  /**
   * Shard component stop was called but component not dead yet
   */
  STOPPING,

  /**
   * Shard component stopped
   */
  DEAD;
}
