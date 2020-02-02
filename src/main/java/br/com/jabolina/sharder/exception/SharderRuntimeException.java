package br.com.jabolina.sharder.exception;

/**
 * Base Runtime exception
 *
 * @author jabolina
 * @date 2/2/20
 */
public class SharderRuntimeException extends SharderException {
  public SharderRuntimeException() {
  }

  public SharderRuntimeException(String message) {
    super(message);
  }

  public SharderRuntimeException(Throwable cause) {
    super(cause);
  }

  /**
   * Thrown when the command destiny still unavailable
   */
  public static class Unavailable extends SharderRuntimeException {
    public Unavailable() {
    }

    public Unavailable(String message) {
      super(message);
    }
  }

  /**
   * Thrown when the command timeouts to be executed
   */
  public static class Timeout extends SharderRuntimeException {
    public Timeout() {
    }

    public Timeout(String message) {
      super(message);
    }
  }

  /**
   * Thrown when an error occurred while executing the command
   */
  public static class ExecutionFailure extends SharderRuntimeException {
    public ExecutionFailure() {
    }

    public ExecutionFailure(String message) {
      super(message);
    }
  }

  /**
   * Thrown when an unknown error occurs
   */
  public static class Unknown extends SharderRuntimeException {
    public Unknown() {
    }

    public Unknown(String message) {
      super(message);
    }
  }
}
