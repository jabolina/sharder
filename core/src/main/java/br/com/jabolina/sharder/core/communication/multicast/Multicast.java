package br.com.jabolina.sharder.core.communication.multicast;

import br.com.jabolina.sharder.core.communication.Communication;

import java.util.function.Consumer;

/**
 * Service for multicast message, this is the base type.
 *
 * @author jabolina
 * @date 1/12/20
 */
public interface Multicast extends Communication {

  /**
   * Multicast the given message
   *
   * @param subject
   * @param message
   */
  void multicast(String subject, byte[] message);

  /**
   * Start listening to the given subject
   *
   * @param subject: subject to listen
   * @param listener: who will listen
   */
  void subscribe(String subject, Consumer<byte[]> listener);

  /**
   * Stop listening messages from the given subject
   *
   * @param subject: subject to unsubscribe
   * @param listener: listener that will leave
   */
  void unsubscribe(String subject, Consumer<byte[]> listener);

  interface Builder extends br.com.jabolina.sharder.core.utils.contract.Builder<MulticastComponent> {
  }
}
