package br.com.jabolina.sharder.core.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author jab
 * @date 1/11/20
 */
public class ConcurrentPool extends BaseConcurrentContext {
  private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentPool.class);
  protected final ScheduledExecutorService executorService;
  private final LinkedList<Runnable> tasks = new LinkedList<>();
  private final Runnable heart;
  private boolean running;
  private final Executor wrap = (Runnable command) -> {
    try {
      synchronized (tasks) {
        tasks.add(command);
        if (!running) {
          running = true;
          ConcurrentPool.this.executorService.execute(command);
        }
      }
    } catch (Exception e) {
      LOGGER.error("Unexpected pool exception", e);
    }
  };

  public ConcurrentPool(ScheduledExecutorService executorService) {
    this.executorService = Objects.requireNonNull(executorService, "Executor service cannot be null!");
    heart = () -> {
      ((Concurrent) Thread.currentThread()).setContext(this);
      for (;;) {
        final Runnable task;
        synchronized (tasks) {
          task = tasks.poll();
          if (task == null) {
            running = false;
            return;
          }
        }

        try {
          task.run();
        } catch (Exception ex) {
          LOGGER.error("Uncaught pool exception", ex);
          throw ex;
        }
      }
    };
  }

  @Override
  public void close() { }

  @Override
  public Cancellable schedule(Duration delay, Duration interval, Runnable runnable) {
    ScheduledFuture<?> future = executorService.scheduleAtFixedRate(
        () -> wrap.execute(runnable),
        delay.toMillis(),
        interval.toMillis(),
        TimeUnit.MILLISECONDS);
    return () -> future.cancel(false);
  }

  @Override
  public void execute(Runnable command) {
    wrap.execute(command);
  }
}
