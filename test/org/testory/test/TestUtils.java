package org.testory.test;

public class TestUtils {
  public static Object newObject(final String name) {
    if (name == null) {
      throw new NullPointerException();
    }
    return new Object() {
      public String toString() {
        return name;
      }
    };
  }

  @SuppressWarnings("serial")
  public static Throwable newThrowable(final String name) {
    if (name == null) {
      throw new NullPointerException();
    }
    return new Throwable() {
      public String toString() {
        return name;
      }
    };
  }
}
