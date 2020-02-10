package br.com.jabolina.sharder.atomix;

import br.com.jabolina.sharder.message.AbstractSharderMessageResponse;
import br.com.jabolina.sharder.primitive.PrimitiveHolder;
import br.com.jabolina.sharder.primitive.data.AbstractPrimitive;

import java.util.concurrent.CompletableFuture;

/**
 * @author jabolina
 * @date 2/9/20
 */
public interface AtomixClient {

  /**
   * Handle request to Atomix
   *
   * @param holder: primitive holder
   * @param primitive: primitive in execution
   * @return future with request response
   */
  CompletableFuture<AbstractSharderMessageResponse> primitive(PrimitiveHolder holder, AbstractPrimitive primitive);
}
