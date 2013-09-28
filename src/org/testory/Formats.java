package org.testory;

import java.lang.reflect.Array;

import org.testory.common.Nullable;

// TODO test Formats
public class Formats {
  public static String formatSection(String caption, @Nullable Object content) {
    return "" //
        + "  " + caption + "\n" //
        + "    " + print(content) + "\n";
  }

  private static String print(@Nullable Object object) {
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
