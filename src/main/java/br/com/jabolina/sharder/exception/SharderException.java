package br.com.jabolina.sharder.exception;

/**
 * @author jabolina
 * @date 2/2/20
 */
public class SharderException extends RuntimeException {
  public SharderException() {
  }

  public SharderException(String message) {
    super(message);
  }

  public SharderException(String message, Throwable cause) {
    super(message, cause);
  }

  public SharderException(Throwable cause) {
    super(cause);
  }

  public SharderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
