package br.com.jabolina.sharder.core.utils.contract;

/**
 * Interface for building components
 *
 * @param <T> type for the building object
 */
public interface Builder<T> {

  /**
   * Build the object, not necessarily a new instance.
   *
   * @return the built object
   */
  T build();
}
