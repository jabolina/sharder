package br.com.jabolina.sharder.communication.multicast;

import br.com.jabolina.sharder.communication.Address;
import br.com.jabolina.sharder.communication.Communication;

import java.util.Objects;
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

  /**
   * Builder for the multicast protocol
   *
   * @param <T> : type of the concrete multicast implementation
   * @param <U> : type of the multicast builder
   */
  @SuppressWarnings("unchecked")
  abstract class Builder<T extends Multicast, U extends Builder<T, U>> implements br.com.jabolina.sharder.utils.contract.Builder<T> {
    protected Address localAddr;
    protected Address groupAddr;

    public U withLocalAddr(Address localAddr) {
      this.localAddr = Objects.requireNonNull(localAddr, "Multicast local address cannot be null!");
      return (U) this;
    }

    public U withGroupAddr(Address groupAddr) {
      this.groupAddr = Objects.requireNonNull(groupAddr, "Multicast group address cannot be null!");
      return (U) this;
    }
  }
}
