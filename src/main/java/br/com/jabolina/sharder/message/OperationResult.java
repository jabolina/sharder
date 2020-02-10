package br.com.jabolina.sharder.message;

import io.atomix.utils.misc.ArraySizeHashPrinter;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Operation result
 *
 * @author jabolina
 * @date 2/9/20
 */
public class OperationResult {
  private final byte[] result;
  private final Throwable error;

  public OperationResult(byte[] result, Throwable error) {
    this.result = result;
    this.error = error;
  }

  public static OperationResult success(byte[] result) {
    return new OperationResult(result, null);
  }

  public static OperationResult error(Throwable error) {
    return new OperationResult(null, error);
  }

  /**
   * Returns the result value.
   *
   * @return The result value.
   */
  public byte[] result() {
    return result;
  }

  /**
   * Returns the operation error.
   *
   * @return the operation error
   */
  public Throwable error() {
    return error;
  }

  /**
   * Returns whether the operation succeeded.
   *
   * @return whether the operation succeeded
   */
  public boolean success() {
    return error == null;
  }

  /**
   * Returns whether the operation failed.
   *
   * @return whether the operation failed
   */
  public boolean failed() {
    return !success();
  }

  @Override
  public String toString() {
    return toStringHelper(this)
        .add("error", error)
        .add("result", ArraySizeHashPrinter.of(result))
        .toString();
  }
}
