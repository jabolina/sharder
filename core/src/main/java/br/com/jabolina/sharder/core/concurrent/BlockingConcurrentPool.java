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
    System.out.println("executing");
    if (isBlocked()) {
      System.out.println("is blocked");
      executorService.execute(command);
    } else {
      System.out.println("not blocked");
      super.execute(command);
    }
  }
}
