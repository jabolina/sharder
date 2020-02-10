package br.com.jabolina.sharder.message.atomix.response;

import br.com.jabolina.sharder.exception.SharderError;
import br.com.jabolina.sharder.message.AbstractSharderMessageResponse;
import br.com.jabolina.sharder.message.atomix.AtomixMessage;

/**
 * @author jabolina
 * @date 2/9/20
 */
public class AtomixQueryResponse extends AbstractSharderMessageResponse implements AtomixMessage {
  private AtomixQueryResponse(Status status, SharderError error, byte[] result) {
    super(status, error, result);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder extends AbstractSharderMessageResponse.Builder<AtomixQueryResponse, Builder> {

    @Override
    public AtomixQueryResponse build() {
      validate();
      return new AtomixQueryResponse(status, error, result);
    }
  }
}
