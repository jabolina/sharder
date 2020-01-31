package br.com.jabolina.sharder.concurrent;

import java.util.concurrent.ThreadFactory;

/**
 * Concurrent factory for naming threads
 *
 * @author jab
 * @date 1/11/20
 */
public class ConcurrentThreadFactory implements ThreadFactory {
  @Override
  public Thread newThread(Runnable r) {
    return new Concurrent(r);
  }
}
