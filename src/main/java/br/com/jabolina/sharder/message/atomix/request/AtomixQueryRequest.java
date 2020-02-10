package br.com.jabolina.sharder.message.atomix.request;

import br.com.jabolina.sharder.exception.SharderException;
import br.com.jabolina.sharder.message.AbstractSharderMessageRequest;
import br.com.jabolina.sharder.message.Operation;
import br.com.jabolina.sharder.message.atomix.AtomixMessage;
import br.com.jabolina.sharder.message.atomix.operation.AbstractAtomixOperation;

/**
 * @author jabolina
 * @date 2/9/20
 */
public class AtomixQueryRequest extends AbstractSharderMessageRequest implements AtomixMessage {
  protected AtomixQueryRequest(Operation operation) {
    super(operation);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder extends AbstractSharderMessageRequest.Builder<AtomixQueryRequest, Builder> {

    @Override
    protected void validate() {
      super.validate();

      if (!(operation instanceof AbstractAtomixOperation)) {
        throw new SharderException("Atomix operation not right for query!");
      }
    }

    @Override
    public AtomixQueryRequest build() {
      validate();
      return new AtomixQueryRequest(operation);
    }
  }
}
