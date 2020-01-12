package br.com.jabolina.sharder.core.concurrent;

import org.slf4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * @author jab
 * @date 1/11/20
 */
public final class ConcurrentPoolFactory implements ConcurrentFactory {
  private final ScheduledExecutorService executorService;

  public static ConcurrentContext poolContext(String name, int poolSize, Logger logger) {
    return new ConcurrentPoolFactory(name, poolSize, logger).context();
  }

  public ConcurrentPoolFactory(String name, int poolSize, Logger logger) {
    this(poolSize, ConcurrentNamingFactory.name(name, logger));
  }

  private ConcurrentPoolFactory(int poolSize, ThreadFactory factory) {
    this(Executors.newScheduledThreadPool(poolSize, factory));
  }

  private ConcurrentPoolFactory(ScheduledExecutorService executorService) {
    this.executorService = executorService;
  }

  @Override
  public ConcurrentContext context() {
    return new BlockingConcurrentPool(executorService);
  }

  @Override
  public void close() {
    executorService.shutdownNow();
  }
}
