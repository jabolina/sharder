package br.com.jabolina.sharder.exception;

/**
 * Expected errors that can be thrown during Sharder execution.
 *
 * @author jabolina
 * @date 2/2/20
 */
public enum Error {

  /**
   * Error when execution could not find the client to deliver
   */
  UNAVAILABLE {
    @Override
    public SharderRuntimeException exception() {
      return exception("Client not available!");
    }

    @Override
    public SharderRuntimeException exception(String message) {
      return message != null ? new SharderRuntimeException.Unavailable(message) : exception();
    }
  },

  /**
   * Timeout while executing the command
   */
  TIMEOUT {
    @Override
    public SharderRuntimeException exception() {
      return exception("Timeout for command execution!");
    }

    @Override
    public SharderRuntimeException exception(String message) {
      return message != null ? new SharderRuntimeException.Timeout(message) : exception();
    }
  },

  /**
   * Error while executing the command
   */
  FAILURE {
    @Override
    public SharderRuntimeException exception() {
      return exception("Error executing command!");
    }

    @Override
    public SharderRuntimeException exception(String message) {
      return message != null ? new SharderRuntimeException.ExecutionFailure(message) : exception();
    }
  },

  NOT_READY {
    @Override
    public SharderRuntimeException exception() {
      return exception("Element not created to be accessed!");
    }

    @Override
    public SharderRuntimeException exception(String message) {
      return message != null ? new SharderRuntimeException.NotReadyException(message) : exception();
    }
  },

  /**
   * Error for unknown exception
   */
  UNKNOWN {
    @Override
    public SharderRuntimeException exception() {
      return exception("An unexpected error occurred!");
    }

    @Override
    public SharderRuntimeException exception(String message) {
      return message != null ? new SharderRuntimeException.Unknown(message) : exception();
    }
  };

  /**
   * Creates the runtime exception for the error with the default message
   *
   * @return Runtime exception
   */
  public abstract SharderRuntimeException exception();

  /**
   * Creates the runtime exception for the error with the given message
   *
   * @param message: message for the exception
   * @return Runtime exception
   */
  public abstract SharderRuntimeException exception(String message);
}
