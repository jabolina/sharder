package br.com.jabolina.sharder.core.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author jab
 * @date 1/11/20
 */
public class SingleConcurrent extends BaseConcurrentContext {
  private static final Logger LOGGER = LoggerFactory.getLogger(SingleConcurrent.class);
  private final ScheduledExecutorService executor;
  private final Executor wrap = (Runnable command) -> {
    try {
      SingleConcurrent.this.executor.execute(() -> {
        try {
          command.run();
        } catch (Exception ex) {
          LOGGER.error("Uncaught exception o wrapper", ex);
        }
      });
    } catch (Exception e) {
      LOGGER.error("Unexpected exception", e);
    }
  };

  public SingleConcurrent(String name) {
    this(ConcurrentNamingFactory.name(name, LOGGER));
  }

  public SingleConcurrent(ThreadFactory factory) {
    this(new ScheduledThreadPoolExecutor(1, factory));
  }

  private SingleConcurrent(ScheduledExecutorService executor) {
    this(get(executor), executor);
  }

  private SingleConcurrent(Thread thread, ScheduledExecutorService executor) {
    this.executor = executor;
    ((Concurrent) thread).setContext(this);
  }

  private static Concurrent get(ExecutorService exec) {
    AtomicReference<Concurrent> concurrent = new AtomicReference<>();
    try {
      exec.submit(() -> concurrent.set((Concurrent) Thread.currentThread())).get();
    } catch (InterruptedException | ExecutionException e) {
      throw new IllegalStateException("Cannot initialize thread", e);
    }

    return concurrent.get();
  }

  @Override
  public Cancellable schedule(Duration delay, Duration interval, Runnable runnable) {
    ScheduledFuture<?> future = executor.scheduleAtFixedRate(runnable, delay.toMillis(), interval.toMillis(), TimeUnit.MILLISECONDS);
    return () -> future.cancel(false);
  }

  @Override
  public void close() {
    executor.shutdownNow();
  }

  @Override
  public void execute(Runnable command) {
    wrap.execute(command);
  }
}
