package br.com.jabolina.sharder.message.atomix;

import br.com.jabolina.sharder.message.SharderMessage;

/**
 * Base interface for all messages exchanged between Sharder and Atomix.
 * This messages will be issued from the Sharder API then be converted to commands that
 * can be executed inside Atomix.
 *
 * @author jabolina
 * @date 2/1/20
 */
public interface AtomixMessage extends SharderMessage {

  /**
   * Basic builder interface for creating Sharder messages
   *
   * @param <T> : type of the operation that will be executed
   * @param <U> : Builder generic type
   */
  interface Builder<T extends AtomixMessage, U extends Builder<T, U>> extends SharderMessage.Builder<T, U> {

  }
}
