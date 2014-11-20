package org.testory.common;

import java.lang.reflect.Array;

public class Objects {
  // TODO test print(Object)
  public static String print(@Nullable Object object) {
    return object == null
        ? "null"
        : object.getClass().isArray()
            ? printArray(object)
            : String.valueOf(object);
  }

  private static String printArray(Object array) {
    StringBuilder builder = new StringBuilder();
    builder.append('[');
    int size = Array.getLength(array);
    for (int i = 0; i < size; i++) {
      builder.append(print(Array.get(array, i))).append(", ");
    }
    if (size > 0) {
      builder.delete(builder.length() - 2, builder.length());
    }
    builder.append(']');
    return builder.toString();
  }
}
