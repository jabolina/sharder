package br.com.jabolina.sharder.core.concurrent;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Enable scheduled runnables
 *
 * @author jab
 * @date 1/11/20
 */
public interface Scheduled {

  Cancellable schedule(Duration delay, Duration interval, Runnable runnable);

  default Cancellable schedule(long delay, long interval, TimeUnit unit, Runnable runnable) {
    return schedule(Duration.ofMillis(unit.toMillis(delay)), Duration.ofMillis(unit.toMillis(interval)), runnable);
  }

  default Cancellable schedule(long interval, TimeUnit unit, Runnable runnable) {
    return schedule(0L, interval, unit, runnable);
  }

  default Cancellable schedule(Duration interval, Runnable runnable) {
    return schedule(Duration.ofMillis(0L), interval, runnable);
  }
}
