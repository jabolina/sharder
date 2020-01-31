package br.com.jabolina.sharder.concurrent;

import java.util.concurrent.Executor;

/**
 * @author jab
 * @date 1/11/20
 */
public interface ConcurrentContext extends AutoCloseable, Executor, Scheduled {
  boolean isBlocked();

  void lock();

  void unlock();

  @Override
  void close();
}
