package org.testory.plumbing.format;

public class Multiline {
  public final Iterable<?> iterable;

  private Multiline(Iterable<?> iterable) {
    this.iterable = iterable;
  }

  public static Multiline multiline(Iterable<?> iterable) {
    return new Multiline(iterable);
  }
}
