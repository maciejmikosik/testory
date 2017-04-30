package org.testory.plumbing.history;

import static org.testory.plumbing.PlumbingException.check;

import org.testory.common.Chain;

public class SynchronizedHistory implements History {
  private final History history;

  private SynchronizedHistory(History history) {
    this.history = history;
  }

  public static History synchronize(History history) {
    check(history != null);
    return new SynchronizedHistory(history);
  }

  public synchronized Chain<Object> get() {
    return history.get();
  }

  public synchronized History add(Object event) {
    return history.add(event);
  }
}
