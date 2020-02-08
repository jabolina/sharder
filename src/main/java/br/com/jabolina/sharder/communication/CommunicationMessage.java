package br.com.jabolina.sharder.communication;

/**
 * @author jabolina
 * @date 2/8/20
 */
public class CommunicationMessage {
  private final String subject;
  private final byte[] payload;

  public CommunicationMessage() {
    this(null, null);
  }

  public CommunicationMessage(String subject, byte[] payload) {
    this.subject = subject;
    this.payload = payload;
  }

  public String subject() {
    return subject;
  }

  public byte[] payload() {
    return payload;
  }
}
