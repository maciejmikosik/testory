package org.testory.common;

public class Checks {
  public static <T> T checkNotNull(T instance) {
    if (instance == null) {
      throw new NullPointerException();
    }
    return instance;
  }

  public static void checkArgument(boolean condition) {
    if (!condition) {
      throw new IllegalArgumentException();
    }
  }
}
