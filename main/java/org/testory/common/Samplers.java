package org.testory.common;

import static org.testory.common.Checks.checkNotNull;
import static org.testory.common.Classes.setAccessible;
import static org.testory.common.Classes.tryWrap;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Random;

public class Samplers {
  public static Sampler fairSampler() {
    return new Sampler() {
      public <T> T sample(Class<T> type, String name) {
        checkNotNull(type);
        checkNotNull(name);
        for (Method method : SampleFactories.class.getDeclaredMethods()) {
          if (tryWrap(method.getReturnType()).isAssignableFrom(tryWrap(type))) {
            try {
              setAccessible(method);
              return (T) method.invoke(null, type, name);
            } catch (ReflectiveOperationException e) {
              throw new LinkageError(null, e);
            }
          }
        }
        throw new IllegalArgumentException("type=" + type + ", name=" + name);
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

  @SuppressWarnings("unused")
  private static class SampleFactories {
    private static boolean sampleBoolean(Class<?> type, String name) {
      return newRandom(name).nextBoolean();
    }

    private static char sampleCharacter(Class<?> type, String name) {
      return (char) ('a' + newRandom(name).nextInt(26));
    }

    private static byte sampleByte(Class<?> type, String name) {
      return (byte) randomInteger(Byte.MAX_VALUE, newRandom(name));
    }

    private static short sampleShort(Class<?> type, String name) {
      return (short) randomInteger(Short.MAX_VALUE, newRandom(name));
    }

    private static int sampleInteger(Class<?> type, String name) {
      return randomInteger(Integer.MAX_VALUE, newRandom(name));
    }

    private static long sampleLong(Class<?> type, String name) {
      return randomInteger(Long.MAX_VALUE, newRandom(name));
    }

    private static float sampleFloat(Class<?> type, String name) {
      return (float) randomDouble(30, newRandom(name));
    }

    private static double sampleDouble(Class<?> type, String name) {
      return randomDouble(300, newRandom(name));
    }

    private static Void sampleVoid(Class<?> type, String name) {
      return null;
    }

    private static String sampleString(Class<?> type, String name) {
      return name;
    }

    private static Class<?> sampleClass(Class<?> type, String name) {
      class SampleClass {
        public Object sampleField;

        public void sampleMethod() {}
      }
      return SampleClass.class;
    }

    private static Method sampleMethod(Class<?> type, String name) throws Exception {
      return sampleClass(type, name).getDeclaredMethods()[0];
    }

    private static Constructor<?> sampleConstructor(Class<?> type, String name) throws Exception {
      return sampleClass(type, name).getDeclaredConstructors()[0];
    }

    private static Field sampleField(Class<?> type, String name) throws Exception {
      return sampleClass(type, name).getDeclaredFields()[0];
    }

    private static Enum<?> sampleEnum(Class<? extends Enum<?>> type, String name) {
      Random random = newRandom(name);
      Enum<?>[] constants = type.getEnumConstants();
      return constants[random.nextInt(constants.length)];
    }
  }

  public static Sampler withSingleElementArray(final Sampler sampler) {
    return new Sampler() {
      public <T> T sample(Class<T> type, String name) {
        return type.isArray()
            ? sampleArray(type, name)
            : sampler.sample(type, name);
      }

      private <T> T sampleArray(Class<T> arrayType, String name) {
        Class<?> componentType = arrayType.getComponentType();
        Object array = Array.newInstance(componentType, 1);
        Object element = sample(componentType, name + "[0]");
        Array.set(array, 0, element);
        return (T) array;
      }
    };
  }
}
