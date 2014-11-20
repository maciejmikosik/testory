package org.testory.common;

import static org.testory.common.Checks.checkNotNull;
import static org.testory.common.Collections.immutable;
import static org.testory.common.Throwables.newLinkageError;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Samples {
  public static boolean isSampleable(Class<?> type) {
    checkNotNull(type);
    return type.isPrimitive() || wrappers.contains(type) || type.isEnum() || type == String.class
        || type == Class.class || AccessibleObject.class.isAssignableFrom(type)
        || type == Void.class;
  }

  public static Object sample(Class<?> type, String name) {
    checkNotNull(type);
    checkNotNull(name);
    return type.isPrimitive() || wrappers.contains(type)
        ? samplePrimitive(type, name)
        : type.isEnum()
            ? sampleEnum(type, name)
            : type == String.class
                ? name
                : type == Class.class || AccessibleObject.class.isAssignableFrom(type)
                    ? sampleReflected(type, name)
                    : type == Void.class
                        ? null
                        : fail(type, name);
  }

  private static final Set<Class<?>> wrappers = immutable(new HashSet<Class<?>>(Arrays.asList(
      Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Long.class,
      Float.class, Double.class)));

  private static Object samplePrimitive(Class<?> type, String name) {
    Random random = new Random(new Random(name.hashCode()).nextLong());
    if (type == boolean.class || type == Boolean.class) {
      return random.nextBoolean();
    } else if (type == char.class || type == Character.class) {
      return (char) ('a' + random.nextInt(26));
    } else if (type == byte.class || type == Byte.class) {
      return (byte) randomInteger(Byte.MAX_VALUE, random);
    } else if (type == short.class || type == Short.class) {
      return (short) randomInteger(Short.MAX_VALUE, random);
    } else if (type == int.class || type == Integer.class) {
      return (int) randomInteger(Integer.MAX_VALUE, random);
    } else if (type == long.class || type == Long.class) {
      return (long) randomInteger(Long.MAX_VALUE, random);
    } else if (type == float.class || type == Float.class) {
      return (float) randomReal(30, random);
    } else if (type == double.class || type == Double.class) {
      return (double) randomReal(300, random);
    }
    return null;
  }

  private static int randomInteger(long maxValue, Random random) {
    int max = (int) Math.floor(Math.pow(maxValue, 1f / 3));
    boolean sign = random.nextBoolean();
    int value = random.nextInt(max - 1) + 2;
    return sign
        ? value
        : -value;
  }

  private static double randomReal(int maxExponent, Random random) {
    boolean sign = random.nextBoolean();
    float exponent = maxExponent * (2 * random.nextFloat() - 1f);
    double value = Math.pow(2, exponent);
    return sign
        ? value
        : -value;
  }

  private static Object sampleEnum(Class<?> type, String name) {
    Random random = new Random(new Random(name.hashCode()).nextLong());
    Object[] constants = type.getEnumConstants();
    return constants[random.nextInt(constants.length)];
  }

  private static Object sampleReflected(Class<?> type, String name) {
    @SuppressWarnings("unused")
    class SampleClass {
      public Object sampleField;

      public void sampleMethod() {}
    }
    try {
      return type == Class.class
          ? SampleClass.class
          : type == Method.class
              ? SampleClass.class.getDeclaredMethod("sampleMethod")
              : type == Constructor.class
                  ? SampleClass.class.getDeclaredConstructor()
                  : type == Field.class
                      ? SampleClass.class.getDeclaredField("sampleField")
                      : fail(type, name);
    } catch (ReflectiveOperationException e) {
      throw newLinkageError(e);
    }
  }

  private static Object fail(Class<?> type, String name) {
    throw new IllegalArgumentException("cannot sample " + type.getName() + " " + name);
  }
}
