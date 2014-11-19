package org.testory.common;

import static org.testory.common.Throwables.newLinkageError;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
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
    try {
      return objectA.getClass() == objectB.getClass()
          && (boolean) arraysEqualsMethod(objectA.getClass()).invoke(null, objectA, objectB);
    } catch (ReflectiveOperationException e) {
      throw newLinkageError(e);
    }
  }

  private static Method arraysEqualsMethod(Class<?> arrayType) {
    try {
      return arrayType.getComponentType().isPrimitive()
          ? Arrays.class.getMethod("equals", arrayType, arrayType)
          : Arrays.class.getMethod("deepEquals", Object[].class, Object[].class);
    } catch (NoSuchMethodException e) {
      throw newLinkageError(e);
    }
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
