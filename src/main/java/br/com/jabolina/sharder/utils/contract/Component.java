package br.com.jabolina.sharder.utils.contract;

import java.util.concurrent.CompletableFuture;

/**
 * Wrapper for a sharder component
 *
 * @param <T> sharder concrete type
 *
 * @author jab
 * @date 1/11/20
 */
public interface Component<T> {

  /**
   * Start component
   *
   * @return future that will return the component when completed
   */
  CompletableFuture<T> start();

  /**
   * Stop component
   *
   * @return future that will stop the component
   */
  CompletableFuture<Void> stop();

  /**
   * Component is running
   *
   * @return true if component is running, false otherwise
   */
  boolean isRunning();
}
