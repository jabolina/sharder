package br.com.jabolina.sharder.communication.multicast;

import br.com.jabolina.sharder.utils.contract.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author jabolina
 * @date 1/12/20
 */
public class MulticastConfiguration implements Configuration {
  private static final String DEFAULT_MULTICAST_IP = "230.0.0.1";
  private static final Integer DEFAULT_MULTICAST_PORT = 54321;
  private InetAddress group;
  private int port;

  public MulticastConfiguration() {
    this.port = DEFAULT_MULTICAST_PORT;
    try {
      this.group = InetAddress.getByName(DEFAULT_MULTICAST_IP);
    } catch (UnknownHostException e) {
      e.printStackTrace();
      group = null;
    }
  }

  public InetAddress getGroup() {
    return group;
  }

  public MulticastConfiguration setGroup(InetAddress group) {
    this.group = group;
    return this;
  }

  public int getPort() {
    return port;
  }

  public MulticastConfiguration setPort(int port) {
    this.port = port;
    return this;
  }
}
