package br.com.jabolina.sharder.core.concurrent;

import java.util.concurrent.ThreadFactory;

/**
 * Concurrent factory for naming threads
 *
 * @author jab
 * @date 1/11/20
 */
public class ConcurrentFactory implements ThreadFactory {
  @Override
  public Thread newThread(Runnable r) {
    return new Concurrent(r);
  }
}
