package br.com.jabolina.sharder.communication;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Provide helpers for address handling
 *
 * @author jabolina
 * @date 1/12/20
 */
public final class Address {
  private static final int DEFAULT_PORT = 5679;
  private final String host;
  private final Integer port;
  private volatile InetAddress address;

  private Address(String host, Integer port) {
    this.host = host;
    this.port = port;
  }

  private Address(String host, Integer port, InetAddress address) {
    this.host = host;
    this.port = port;
    this.address = address;
  }

  public static Address local() {
    return from(localhost(), DEFAULT_PORT);
  }

  public static Address from(Integer port) {
    return from(localhost(), port);
  }

  public static Address from(String host) {
    return from(host, DEFAULT_PORT);
  }

  public static Address from(String host, Integer port, InetAddress address) {
    return new Address(host, port, address);
  }

  public static Address from(String host, Integer port) {
    return new Address(host, port);
  }

  private static String localhost() {
    try {
      return inet().getHostName();
    } catch (UnknownHostException e) {
      throw new IllegalArgumentException("Unable to locate host address", e);
    }
  }

  private static InetAddress inet() throws UnknownHostException {
    try {
      return InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      return InetAddress.getByName(null);
    }
  }

  private InetAddress acquire() {
    try {
      return InetAddress.getByName(host);
    } catch (UnknownHostException ignore) {
      return null;
    }
  }

  /**
   * Resolve inet address from host
   *
   * @return inet address
   */
  private synchronized InetAddress resolve() {
    if (address == null) {
      address = acquire();
    }

    return address;
  }

  /**
   * Get address as inet address
   *
   * @return inet address for the host and port
   */
  public InetAddress address() {
    return resolve();
  }

  /**
   * Get address host
   *
   * @return address host
   */
  public String getHost() {
    return host;
  }

  /**
   * Get address port
   *
   * @return address port
   */
  public Integer getPort() {
    return port;
  }

  @Override
  public String toString() {
    return String.format("Address[%s:%d]", host, port);
  }
}
