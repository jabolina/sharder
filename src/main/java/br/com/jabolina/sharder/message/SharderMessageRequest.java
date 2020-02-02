package br.com.jabolina.sharder.message;

/**
 * @author jabolina
 * @date 2/2/20
 */
public interface SharderMessageRequest extends SharderMessage {

  interface Builder<T extends SharderMessageRequest, U extends Builder<T, U>>
      extends br.com.jabolina.sharder.utils.contract.Builder<T> {

    /**
     * Returns the request operation
     *
     * @return request operation
     */
    Operation operation();

    /**
     * Sets the request operation
     *
     * @param operation: operation to be executed
     * @return The request builder
     * @throws NullPointerException if {@code operation} is null
     */
    U withOperation(Operation operation);
  }
}
