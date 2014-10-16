package org.testory.common;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Objects {
  public static boolean areEqual(@Nullable Object objectA, @Nullable Object objectB) {
    return objectA == null
        ? objectB == null
        : objectA.equals(objectB);
  }

  public static boolean areEqualDeep(@Nullable Object objectA, @Nullable Object objectB) {
    return objectA == null
        ? objectB == null
        : objectA.getClass().isArray()
            ? objectB != null && areEqualArrays(objectA, objectB)
            : objectA.equals(objectB);
  }

  private static boolean areEqualArrays(Object objectA, Object objectB) {
    Class<?> type = objectA.getClass().getComponentType();
    return type != objectB.getClass().getComponentType()
        ? false
        : type == boolean.class
            ? Arrays.equals((boolean[]) objectA, (boolean[]) objectB)
            : type == char.class
                ? Arrays.equals((char[]) objectA, (char[]) objectB)
                : type == byte.class
                    ? Arrays.equals((byte[]) objectA, (byte[]) objectB)
                    : type == short.class
                        ? Arrays.equals((short[]) objectA, (short[]) objectB)
                        : type == int.class
                            ? Arrays.equals((int[]) objectA, (int[]) objectB)
                            : type == long.class
                                ? Arrays.equals((long[]) objectA, (long[]) objectB)
                                : type == float.class
                                    ? Arrays.equals((float[]) objectA, (float[]) objectB)
                                    : type == double.class
                                        ? Arrays.equals((double[]) objectA, (double[]) objectB)
                                        : Arrays.deepEquals((Object[]) objectA, (Object[]) objectB);
  }

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
