package org.testory.plumbing.mock;

import static org.testory.plumbing.PlumbingException.check;

public class Mocked {
  public final Object mock;
  public final String name;

  private Mocked(Object mock, String name) {
    this.mock = mock;
    this.name = name;
  }

  public static Mocked mocked(Object mock, String name) {
    check(mock != null);
    check(name != null);
    return new Mocked(mock, name);
  }

  public String toString() {
    return "mocked(" + mock + ", " + name + ")";
  }
}
