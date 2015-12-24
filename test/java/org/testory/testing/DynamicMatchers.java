package org.testory.testing;

import static java.text.MessageFormat.format;
import static java.util.Objects.deepEquals;

import java.util.Arrays;

public class DynamicMatchers {
  public static Object same(final Object expected) {
    return new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object item) {
        return item == expected;
      }

      public String toString() {
        return format("same({0})", expected);
      }
    };
  }

  public static Object deepEqual(final Object object) {
    return new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object item) {
        return deepEquals(object, item);
      }

      public String toString() {
        return format("deepEqual({0})", object);
      }
    };
  }

  public static Object number(final Integer... numbers) {
    return new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object item) {
        return Arrays.asList(numbers).contains(item);
      }

      public String toString() {
        return format("number({0})", Arrays.toString(numbers));
      }
    };
  }
}
