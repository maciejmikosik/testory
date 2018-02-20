package org.testory.plumbing.wildcard;

public class Consumed {
  private Consumed() {}

  public static Consumed consumed() {
    return new Consumed();
  }

  public String toString() {
    return "consumed()";
  }
}
