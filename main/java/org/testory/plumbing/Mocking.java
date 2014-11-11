package org.testory.plumbing;

import static org.testory.plumbing.PlumbingException.check;

public class Mocking {
  public final Object mock;

  private Mocking(Object mock) {
    this.mock = mock;
  }

  public static Mocking mocking(Object mock) {
    check(mock != null);
    return new Mocking(mock);
  }

  public String toString() {
    return "mocking(" + mock + ")";
  }

  public static boolean isMock(Object mock, History history) {
    check(mock != null);
    check(history != null);
    for (Object event : history.events) {
      if (event instanceof Mocking) {
        Mocking mocking = (Mocking) event;
        if (mocking.mock == mock) {
          return true;
        }
      }
    }
    return false;
  }
}
