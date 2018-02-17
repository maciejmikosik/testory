package org.testory.plumbing.wildcard;

public class Wildcarded {
  private Wildcarded() {}

  public static Wildcarded wildcarded() {
    return new Wildcarded();
  }

  public String toString() {
    return "wildcarded()";
  }
}
