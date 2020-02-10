package br.com.jabolina.sharder.message.atomix.response;

import br.com.jabolina.sharder.exception.SharderError;
import br.com.jabolina.sharder.message.AbstractSharderMessageResponse;
import br.com.jabolina.sharder.message.atomix.AtomixMessage;

/**
 * Atomix client response.
 *
 * @author jabolina
 * @date 2/2/20
 */
public class AtomixExecuteResponse extends AbstractSharderMessageResponse implements AtomixMessage {
  private AtomixExecuteResponse(Status status, SharderError error, byte[] result) {
    super(status, error, result);
  }

  /**
   * Returns a new submit response builder.
   *
   * @return A new submit response builder.
   */
  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder extends AbstractSharderMessageResponse.Builder<AtomixExecuteResponse, Builder> {

    @Override
    public AtomixExecuteResponse build() {
      validate();
      return new AtomixExecuteResponse(status, error, result);
    }
  }
}
