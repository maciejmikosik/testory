package org.testory.testing;

public class Fakes {
  public static Object newObject(final String name) {
    if (name == null) {
      throw new NullPointerException();
    }

    return new Object() {
      public boolean equals(Object obj) {
        return getClass().isInstance(obj) && toString().equals(obj.toString());
      }

      public int hashCode() {
        return name.hashCode();
      }

      public String toString() {
        return name;
      }
    };
  }

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
