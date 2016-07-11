package org.testory.plumbing.inject;

import static java.lang.String.format;
import static org.testory.common.Checks.checkNotNull;

import java.util.Random;

import org.testory.plumbing.Maker;

public class PrimitiveMaker {
  public static Maker randomPrimitiveMaker() {
    return new Maker() {
      public <T> T make(Class<T> type, String name) {
        checkNotNull(type);
        checkNotNull(name);
        Random random = newRandom(name);
        if (type == Boolean.class || type == boolean.class) {
          return (T) (Boolean) random.nextBoolean();
        } else if (type == Character.class || type == char.class) {
          return (T) (Character) (char) ('a' + random.nextInt(26));
        } else if (type == Byte.class || type == byte.class) {
          return (T) (Byte) (byte) randomInteger(Byte.MAX_VALUE, random);
        } else if (type == Short.class || type == short.class) {
          return (T) (Short) (short) randomInteger(Short.MAX_VALUE, random);
        } else if (type == Integer.class || type == int.class) {
          return (T) (Integer) randomInteger(Integer.MAX_VALUE, random);
        } else if (type == Long.class || type == long.class) {
          return (T) (Long) (long) randomInteger(Long.MAX_VALUE, random);
        } else if (type == Float.class || type == float.class) {
          return (T) (Float) (float) randomDouble(30, random);
        } else if (type == Double.class || type == double.class) {
          return (T) (Double) randomDouble(300, random);
        }
        throw new RuntimeException(format("cannot make %s of type %s", name, type.getName()));
      }
    };
  }

  private static int randomInteger(long maxValue, Random random) {
    int max = (int) Math.floor(Math.pow(maxValue, 1f / 3));
    int value = random.nextInt(max - 1) + 2;
    int sign = random.nextInt(2) * 2 - 1;
    return sign * value;
  }

  private static double randomDouble(int maxExponent, Random random) {
    float exponent = maxExponent * (2 * random.nextFloat() - 1f);
    double value = Math.pow(2, exponent);
    int sign = random.nextInt(2) * 2 - 1;
    return sign * value;
  }

  private static Random newRandom(String seed) {
    return new Random(new Random(seed.hashCode()).nextLong());
  }
}
