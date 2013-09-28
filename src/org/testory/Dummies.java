package org.testory;

import static java.util.Collections.unmodifiableMap;
import static org.testory.Dummies.Signature.signature;
import static org.testory.mock.Mocks.mock;
import static org.testory.mock.Typing.typing;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.testory.mock.Handler;
import org.testory.mock.Invocation;
import org.testory.mock.Typing;

public class Dummies {
  public static Object dummy(Class<?> type, String name) {
    return dummy(signature(type, name));
  }

  private static Object dummy(Signature signature) {
    return reusableDummies.containsKey(signature.type)
        ? reusableDummies.get(signature.type)
        : signature.type.isArray()
            ? dummyArray(signature)
            : Modifier.isFinal(signature.type.getModifiers())
                ? dummyFinal(signature)
                : dummyMock(signature);
  }

  private static Map<Class<?>, Object> reusableDummies = reusableDummies();

  private static Map<Class<?>, Object> reusableDummies() {
    Map<Class<?>, Object> map = new HashMap<Class<?>, Object>();

    map.put(boolean.class, Boolean.FALSE);
    map.put(char.class, Character.valueOf((char) 0));
    map.put(byte.class, Byte.valueOf((byte) 0));
    map.put(short.class, Short.valueOf((short) 0));
    map.put(int.class, Integer.valueOf(0));
    map.put(long.class, Long.valueOf(0));
    map.put(float.class, Float.valueOf(0));
    map.put(double.class, Double.valueOf(0));

    map.put(Void.class, null);
    map.put(Boolean.class, Boolean.FALSE);
    map.put(Character.class, Character.valueOf((char) 0));
    map.put(Byte.class, Byte.valueOf((byte) 0));
    map.put(Short.class, Short.valueOf((short) 0));
    map.put(Integer.class, Integer.valueOf(0));
    map.put(Long.class, Long.valueOf(0));
    map.put(Float.class, Float.valueOf(0));
    map.put(Double.class, Double.valueOf(0));

    try {
      map.put(Class.class, DummyClass.class);
      map.put(Method.class, DummyClass.class.getDeclaredMethod("dummyMethod"));
      map.put(Field.class, DummyClass.class.getDeclaredField("dummyField"));
    } catch (Exception e) {
      throw new Error(e);
    }

    return unmodifiableMap(map);
  }

  private static Object dummyArray(Signature signature) {
    Class<?> componentType = signature.type.getComponentType();
    Object array = Array.newInstance(componentType, 1);
    Array.set(array, 0, dummy(signature(componentType, signature.name)));
    return array;
  }

  private static Object dummyFinal(Signature signature) {
    return signature.type == String.class
        ? signature.name
        : failCreatingDummy(signature);
  }

  private static Object dummyMock(final Signature signature) {
    return mock(typingCastableTo(signature.type), new Handler() {
      public Object handle(Invocation invocation) {
        if (invocation.method.getName().equals("toString")) {
          return signature.name;
        }
        if (invocation.method.getName().equals("equals") && invocation.arguments.size() == 1) {
          return invocation.instance == invocation.arguments.get(0);
        }
        if (invocation.method.getName().equals("hashCode") && invocation.arguments.size() == 0) {
          return signature.name.hashCode();
        }
        return null;
      }
    });
  }

  private static Typing typingCastableTo(Class<?> type) {
    return type.isInterface()
        ? typing(Object.class, new HashSet<Class<?>>(Arrays.asList(type)))
        : typing(type, new HashSet<Class<?>>());
  }

  private static Object failCreatingDummy(Signature signature) {
    throw new IllegalArgumentException("failed creating dummy for field: "
        + signature.type.getSimpleName() + " " + signature.name);
  }

  class DummyClass {
    public Object dummyField;

    public void dummyMethod() {}
  }

  static class Signature {
    public final Class<?> type;
    public final String name;

    private Signature(Class<?> type, String name) {
      this.type = type;
      this.name = name;
    }

    public static Signature signature(Class<?> type, String name) {
      return new Signature(type, name);
    }
  }
}
