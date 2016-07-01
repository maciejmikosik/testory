package org.testory.plumbing.inject;

import static java.lang.String.format;
import static org.testory.plumbing.PlumbingException.check;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Random;

import org.testory.plumbing.Maker;

public class FinalMaker {
  public static Maker finalMaker() {
    return new Maker() {
      public <T> T make(Class<T> type, String name) {
        check(type != null);
        check(name != null);
        if (type == Void.class) {
          return null;
        } else if (type == String.class) {
          return (T) name;
        } else if (type == Class.class) {
          return (T) SampleClass.class;
        } else if (type == Method.class) {
          return (T) SampleClass.class.getDeclaredMethods()[0];
        } else if (type == Constructor.class) {
          return (T) SampleClass.class.getDeclaredConstructors()[0];
        } else if (type == Field.class) {
          return (T) SampleClass.class.getDeclaredFields()[0];
        } else if (Enum.class.isAssignableFrom(type)) {
          return (T) randomEnum((Class<? extends Enum<?>>) type, name);
        }
        throw new RuntimeException(format("unknown type %s", type));
      }
    };
  }

  @SuppressWarnings("unused")
  private static class SampleClass {
    public Object sampleField;

    public void sampleMethod() {}
  }

  private static Enum<?> randomEnum(Class<? extends Enum<?>> type, String name) {
    Random random = newRandom(name);
    Enum<?>[] constants = type.getEnumConstants();
    return constants[random.nextInt(constants.length)];
  }

  private static Random newRandom(String seed) {
    return new Random(new Random(seed.hashCode()).nextLong());
  }
}
