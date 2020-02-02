package br.com.jabolina.sharder.message;

import br.com.jabolina.sharder.exception.SharderError;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * All responses messages must extends this class
 *
 * @author jabolina
 * @date 2/2/20
 */
public abstract class AbstractSharderMessageResponse implements SharderMessageResponse {
  protected final Status status;
  protected final SharderError error;
  protected final byte[] result;

  public AbstractSharderMessageResponse(Status status, SharderError error) {
    this(status, error, null);
  }

  public AbstractSharderMessageResponse(Status status, byte[] result) {
    this(status, null, result);
  }

  public AbstractSharderMessageResponse(Status status, SharderError error, byte[] result) {
    this.status = status;
    this.error = error;
    this.result = result;
  }

  @Override
  public byte[] result() {
    return result;
  }

  @Override
  public Status status() {
    return status;
  }

  @Override
  public SharderError error() {
    return error;
  }

  @Override
  public String toString() {
    if (status == Status.OK) {
      return toStringHelper(this)
          .add("status", status)
          .toString();
    }

    return toStringHelper(this)
        .add("status", status)
        .add("error", "failed")
        .toString();
  }

  @SuppressWarnings("unchecked")
  protected abstract static class Builder<T extends AbstractSharderMessageResponse, U extends Builder<T, U>>
      implements SharderMessageResponse.Builder<T, U> {
    protected Status status;
    protected SharderError error;
    protected byte[] result;

    @Override
    public U withStatus(Status status) {
      this.status = Objects.requireNonNull(status, "Response status cannot be null!");
      return (U) this;
    }

    @Override
    public U withError(SharderError error) {
      this.error = error;
      return (U) this;
    }

    @Override
    public U withResult(byte[] result) {
      this.result = result;
      return (U) this;
    }

    protected void validate() {
      checkNotNull(status, "Status cannot be null!");
    }

    @Override
    public String toString() {
      return toStringHelper(this)
          .add("status", status)
          .add("result", new String(result, StandardCharsets.UTF_8))
          .toString();
    }
  }
}
