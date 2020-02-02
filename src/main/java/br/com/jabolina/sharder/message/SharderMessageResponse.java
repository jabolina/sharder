package br.com.jabolina.sharder.message;

import br.com.jabolina.sharder.exception.Error;
import br.com.jabolina.sharder.exception.SharderError;

/**
 * Base response type for all requests
 *
 * @author jabolina
 * @date 2/2/20
 */
public interface SharderMessageResponse extends SharderMessage {

  enum Status {
    /**
     * Request executed successfully
     */
    OK(1),

    /**
     * An error occurred
     */
    ERROR(0);

    /**
     * Returns the status for the given identifier.
     *
     * @param id The status identifier.
     * @return The status for the given identifier.
     * @throws IllegalArgumentException if {@code id} is not 0 or 1
     */
    public static Status forId(int id) {
      switch (id) {
        case 1:
          return OK;
        case 0:
          return ERROR;
        default: break;
      }

      throw new IllegalArgumentException("invalid response status " + id);
    }

    private final byte id;

    Status(int id) {
      this.id = (byte) id;
    }

    /**
     * Returns the status identifier.
     *
     * @return The status identifier.
     */
    public byte id() {
      return id;
    }
  }

  /**
   * Returns the status for the request
   *
   * @return response status
   */
  Status status();

  /**
   * Returns the response error, if status is {@link Status#ERROR}
   *
   * @return response error, null if executed successfully
   */
  SharderError error();

  /**
   * Returns the response result
   *
   * @return response result
   */
  byte[] result();

  interface Builder<T extends SharderMessageResponse, U extends Builder<T, U>> extends SharderMessage.Builder<T, U> {
    /**
     * Sets the response status.
     *
     * @param status The response status.
     * @return The response builder.
     * @throws NullPointerException if {@code status} is null
     */
    U withStatus(Status status);

    /**
     * Sets the response error.
     *
     * @param error The response error.
     * @return The response builder.
     * @throws NullPointerException if {@code error} is null
     */
    U withError(SharderError error);

    /**
     * Sets the response error.
     *
     * @param error The response error type.
     * @return The response builder.
     * @throws NullPointerException if {@code error} is null
     */
    default U withError(Error error) {
      return withError(new SharderError(error, null));
    }

    /**
     * Sets the response error.
     *
     * @param error The response error type.
     * @param message The error message
     * @return The response builder.
     * @throws NullPointerException if {@code error} is null
     */
    default U withError(Error error, String message) {
      return withError(new SharderError(error, message));
    }
  }
}
