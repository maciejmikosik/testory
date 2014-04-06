package org.testory.common;

import static org.testory.common.Checks.checkArgument;
import static org.testory.common.Checks.checkNotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Classes {
  /* untested */
  public static boolean isPublic(Class<?> type) {
    checkNotNull(type);
    return Modifier.isPublic(type.getModifiers());
  }

  /* untested */
  public static boolean isFinal(Class<?> type) {
    checkNotNull(type);
    return Modifier.isFinal(type.getModifiers());
  }

  public static boolean canAssign(@Nullable Object instance, Class<?> type) {
    checkNotNull(type);
    return type.isPrimitive()
        ? canConvert(instance, type)
        : instance == null || type.isAssignableFrom(instance.getClass());
  }

  private static boolean canConvert(Object instance, Class<?> type) {
    checkArgument(type.isPrimitive());
    if (type == void.class) {
      return false;
    }
    try {
      @SuppressWarnings("unused")
      class Methods {
        // @formatter:off
        void method(boolean a) {}
        void method(char a) {}
        void method(byte a) {}
        void method(short a) {}
        void method(int a) {}
        void method(long a) {}
        void method(float a) {}
        void method(double a) {}
        // @formatter:on
      }

      Methods.class.getDeclaredMethod("method", type).invoke(new Methods(), instance);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    } catch (Exception e) {
      throw new Error(e);
    }
  }

  public static boolean canReturn(@Nullable Object object, Method method) {
    checkNotNull(method);
    return canAssign(object, method.getReturnType());
  }

  public static boolean canThrow(Throwable throwable, Method method) {
    checkNotNull(throwable);
    checkNotNull(method);
    for (Class<?> exceptionType : method.getExceptionTypes()) {
      if (exceptionType.isInstance(throwable)) {
        return true;
      }
    }
    return throwable instanceof RuntimeException || throwable instanceof Error;
  }

  public static boolean canInvoke(Method method, @Nullable Object instance, Object... arguments) {
    checkNotNull(method);
    checkNotNull(arguments);
    return correctInstance(instance, method) && correctArguments(arguments, method);
  }

  private static boolean correctInstance(@Nullable Object instance, Method method) {
    return Modifier.isStatic(method.getModifiers())
        || method.getDeclaringClass().isInstance(instance);
  }

  private static boolean correctArguments(Object[] arguments, Method method) {
    Class<?>[] parameters = method.getParameterTypes();
    if (parameters.length != arguments.length) {
      return false;
    }
    for (int i = 0; i < parameters.length; i++) {
      if (!canAssign(arguments[i], parameters[i])) {
        return false;
      }
    }
    return true;
  }

  public static <T> T zeroOrNull(Class<T> type) {
    checkNotNull(type);
    return (T) zeroes.get(type);
  }

  private static final Map<Class<?>, Object> zeroes = zeroes();

  private static Map<Class<?>, Object> zeroes() {
    Map<Class<?>, Object> map = new HashMap<Class<?>, Object>();
    map.put(boolean.class, Boolean.valueOf(false));
    map.put(char.class, Character.valueOf((char) 0));
    map.put(byte.class, Byte.valueOf((byte) 0));
    map.put(short.class, Short.valueOf((short) 0));
    map.put(int.class, Integer.valueOf(0));
    map.put(long.class, Long.valueOf(0));
    map.put(float.class, Float.valueOf(0));
    map.put(double.class, Double.valueOf(0));
    map.put(Boolean.class, Boolean.valueOf(false));
    map.put(Character.class, Character.valueOf((char) 0));
    map.put(Byte.class, Byte.valueOf((byte) 0));
    map.put(Short.class, Short.valueOf((short) 0));
    map.put(Integer.class, Integer.valueOf(0));
    map.put(Long.class, Long.valueOf(0));
    map.put(Float.class, Float.valueOf(0));
    map.put(Double.class, Double.valueOf(0));
    return Collections.unmodifiableMap(map);
  }

  // TODO test
  public static Class<?> tryWrap(Class<?> type) {
    checkNotNull(type);
    Class<?> wrappedOrNull = wrapping.get(type);
    return wrappedOrNull != null
        ? wrappedOrNull
        : type;
  }

  private static final Map<Class<?>, Class<?>> wrapping = wrapping();

  private static Map<Class<?>, Class<?>> wrapping() {
    Map<Class<?>, Class<?>> map = new HashMap<Class<?>, Class<?>>();
    map.put(void.class, Void.class);
    map.put(boolean.class, Boolean.class);
    map.put(char.class, Character.class);
    map.put(byte.class, Byte.class);
    map.put(short.class, Short.class);
    map.put(int.class, Integer.class);
    map.put(long.class, Long.class);
    map.put(float.class, Float.class);
    map.put(double.class, Double.class);
    return Collections.unmodifiableMap(map);
  }
}
