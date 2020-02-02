package br.com.jabolina.sharder.message;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base request type
 *
 * @author jabolina
 * @date 2/2/20
 */
public abstract class AbstractSharderMessageRequest implements SharderMessageRequest {
  protected final Operation operation;

  protected AbstractSharderMessageRequest(Operation operation) {
    this.operation = operation;
  }

  @SuppressWarnings("unchecked")
  protected abstract static class Builder<T extends AbstractSharderMessageRequest, U extends Builder<T, U>>
      implements SharderMessageRequest.Builder<T, U> {
    protected Operation operation;

    @Override
    public Operation operation() {
      return operation;
    }

    @Override
    public U withOperation(Operation operation) {
      this.operation = Objects.requireNonNull(operation, "Operation cannot be null!");
      return (U) this;
    }

    protected void validate() {
      checkNotNull(operation, "Operation is null!");
    }
  }
}
