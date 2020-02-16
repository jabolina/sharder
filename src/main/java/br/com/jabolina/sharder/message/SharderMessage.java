package br.com.jabolina.sharder.message;

import java.io.Serializable;

/**
 * Base generic type for all sharder exchanged messages
 *
 * @author jabolina
 * @date 2/1/20
 */
public interface SharderMessage extends Serializable {

  /**
   * Basic builder interface for creating Sharder messages
   *
   * @param <T> : type of the operation that will be executed
   * @param <U> : Builder generic type
   */
  interface Builder<T extends SharderMessage, U extends Builder<T, U>> extends br.com.jabolina.sharder.utils.contract.Builder<T> {
    /**
     * Sets response result
     *
     * @param result: The response result
     * @return The response builder.
     * @throws NullPointerException if {@code status} is null
     */
    U withResult(byte[] result);
  }
}
