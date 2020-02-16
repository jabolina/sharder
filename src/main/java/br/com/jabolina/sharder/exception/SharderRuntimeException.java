package br.com.jabolina.sharder.exception;

/**
 * Base Runtime exception
 *
 * @author jabolina
 * @date 2/2/20
 */
public class SharderRuntimeException extends SharderException {
  private final Error error;

  public SharderRuntimeException(Error error) {
    this.error = error;
  }

  public SharderRuntimeException(Error error, String message) {
    super(message);
    this.error = error;
  }

  public SharderRuntimeException(Error error, Throwable cause) {
    super(cause);
    this.error = error;
  }

  /**
   * Returns the exception type
   *
   * @return The exception type
   */
  public Error error() {
    return error;
  }

  /**
   * Thrown when the command destiny still unavailable
   */
  public static class Unavailable extends SharderRuntimeException {
    public Unavailable() {
      this("Sharder client unavailable");
    }

    public Unavailable(String message) {
      super(Error.UNAVAILABLE, message);
    }
  }

  /**
   * Thrown when the command timeouts to be executed
   */
  public static class Timeout extends SharderRuntimeException {
    public Timeout() {
      this("Sharder client timed out");
    }

    public Timeout(String message) {
      super(Error.TIMEOUT, message);
    }
  }

  /**
   * Thrown when an error occurred while executing the command
   */
  public static class ExecutionFailure extends SharderRuntimeException {
    public ExecutionFailure() {
      this("Failed executing operation");
    }

    public ExecutionFailure(String message) {
      super(Error.FAILURE, message);
    }
  }

  /**
   * Thrown when the element to be accessed is not created or is not ready yet
   */
  public static class NotReadyException extends SharderRuntimeException {

    public NotReadyException(String message) {
      super(Error.NOT_READY, message);
    }
  }

  public static class WrongUsageException extends SharderRuntimeException {

    public WrongUsageException(String message) {
      super(Error.WRONG_USAGE, message);
    }
  }

  /**
   * Thrown when an unknown error occurs
   */
  public static class Unknown extends SharderRuntimeException {
    public Unknown() {
      this("Unknown error occurred");
    }

    public Unknown(String message) {
      super(Error.UNKNOWN, message);
    }
  }
}
