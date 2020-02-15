package br.com.jabolina.sharder.concurrent;

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

  public ConcurrentContext getContext() {
    return context != null
        ? context.get()
        : null;
  }

  public Concurrent setContext(ConcurrentContext context) {
    this.context = new WeakReference<>(context);
    return this;
  }
}
