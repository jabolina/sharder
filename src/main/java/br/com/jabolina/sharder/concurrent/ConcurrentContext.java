package br.com.jabolina.sharder.concurrent;

import java.util.concurrent.Executor;

/**
 * @author jab
 * @date 1/11/20
 */
public interface ConcurrentContext extends AutoCloseable, Executor, Scheduled {
  static ConcurrentContext currentContext() {
    Thread thread = Thread.currentThread();
    return thread instanceof Concurrent
        ? ((Concurrent) thread).getContext()
        : null;
  }
  default boolean isCurrentContext() {
    return currentContext() == this;
  }
  boolean isBlocked();

  void lock();

  void unlock();

  @Override
  void close();
}
