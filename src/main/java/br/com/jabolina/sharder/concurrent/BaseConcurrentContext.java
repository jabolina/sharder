package br.com.jabolina.sharder.concurrent;

/**
 * @author jab
 * @date 1/11/20
 */
public abstract class BaseConcurrentContext implements ConcurrentContext {
  private volatile boolean blocked;

  @Override
  public boolean isBlocked() {
    return blocked;
  }

  @Override
  public void lock() {
    blocked = true;
  }

  @Override
  public void unlock() {
    blocked = false;
  }
}
