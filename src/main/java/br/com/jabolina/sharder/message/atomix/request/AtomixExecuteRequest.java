package br.com.jabolina.sharder.message.atomix.request;

import br.com.jabolina.sharder.exception.SharderException;
import br.com.jabolina.sharder.message.AbstractSharderMessageRequest;
import br.com.jabolina.sharder.message.atomix.AtomixOperation;

/**
 * Atomix client request
 *
 * @author jabolina
 * @date 2/2/20
 */
public class AtomixExecuteRequest extends AbstractSharderMessageRequest {
  protected AtomixExecuteRequest(AtomixOperation operation) {
    super(operation);
  }

  /**
   * Returns Atomix request builder
   *
   * @return atomix request builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder for Atomix request operations
   */
  public static final class Builder extends AbstractSharderMessageRequest.Builder<AtomixExecuteRequest, Builder> {

    @Override
    protected void validate() {
      super.validate();

      if (!(operation instanceof AtomixOperation)) {
        throw new SharderException("Atomix operation not right!");
      }
    }

    @Override
    public AtomixExecuteRequest build() {
      validate();
      return new AtomixExecuteRequest((AtomixOperation) operation);
    }
  }
}
