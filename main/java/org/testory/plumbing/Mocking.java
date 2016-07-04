package org.testory.plumbing;

import static org.testory.plumbing.PlumbingException.check;

public class Mocking {
  public final Object mock;
  public final String name;

  private Mocking(Object mock, String name) {
    this.mock = mock;
    this.name = name;
  }

  public static Mocking mocking(Object mock, String name) {
    check(mock != null);
    check(name != null);
    return new Mocking(mock, name);
  }

  public String toString() {
    return "mocking(" + mock + ", " + name + ")";
  }
}
