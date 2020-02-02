package br.com.jabolina.sharder.exception;

import java.util.Objects;

/**
 * All errors on Sharder will be created here.
 *
 * @author jabolina
 * @date 2/2/20
 */
public final class SharderError {
  private final Error error;
  private final String message;

  public SharderError(Error error, String message) {
    this.error = Objects.requireNonNull(error, "Error cannot be null!");
    this.message = message;
  }

  /**
   * Get the error enum object
   *
   * @return the enum error
   */
  public Error error() {
    return error;
  }

  /**
   * Get the message for the exception
   *
   * @return the message for the exception
   */
  public String message() {
    return message;
  }

  /**
   * Get the runtime exception
   *
   * @return the runtime exception
   */
  public SharderRuntimeException exception() {
    return error.exception(message);
  }
}
