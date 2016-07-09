package org.testory.plumbing.capture;

public class ConsumingAny {
  private ConsumingAny() {}

  public static ConsumingAny consumingAny() {
    return new ConsumingAny();
  }

  public String toString() {
    return "consumingAny()";
  }
}
