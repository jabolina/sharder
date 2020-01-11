package br.com.jabolina.sharder.core.concurrent;

import java.lang.ref.WeakReference;

/**
 * @author jab
 * @date 1/11/20
 */
public class Concurrent extends Thread {
  private WeakReference<ConcurrentContext> context;

  public Concurrent(Runnable r) {
    super(r);
  }

  public WeakReference<ConcurrentContext> getContext() {
    return context;
  }

  public Concurrent setContext(ConcurrentContext context) {
    this.context = new WeakReference<>(context);
    return this;
  }
}
