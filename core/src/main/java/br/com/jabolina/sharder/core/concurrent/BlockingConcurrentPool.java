package br.com.jabolina.sharder.core.concurrent;

import java.util.concurrent.ScheduledExecutorService;

/**
 * @author jab
 * @date 1/11/20
 */
public class BlockingConcurrentPool extends ConcurrentPool {
  public BlockingConcurrentPool(ScheduledExecutorService executorService) {
    super(executorService);
  }

  @Override
  public void execute(Runnable command) {
    if (isBlocked()) {
      executorService.execute(command);
    } else {
      super.execute(command);
    }
  }
}
