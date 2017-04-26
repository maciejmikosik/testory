package org.testory.plumbing.capture;

public class Capturing {
  private Capturing() {}

  public static Capturing capturing() {
    return new Capturing();
  }

  public String toString() {
    return "capturing()";
  }
}
