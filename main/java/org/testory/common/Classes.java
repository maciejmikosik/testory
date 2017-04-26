package org.testory.common;

import static java.util.Arrays.asList;
import static org.testory.common.Checks.checkArgument;
import static org.testory.common.Checks.checkNotNull;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Classes {
  public static void setAccessible(final AccessibleObject accessible) {
    checkNotNull(accessible);
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
      public Void run() {
        accessible.setAccessible(true);
        return null;
      }
    });
  }

  public static boolean hasMethod(String name, Class<?>[] parameters, Class<?> type) {
    checkNotNull(name);
    checkNotNull(parameters);
    checkArgument(!asList(parameters).contains(null));
    checkNotNull(type);
    try {
      type.getMethod(name, parameters);
      return true;
    } catch (NoSuchMethodException e) {
      return false;
    }
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
    } catch (ReflectiveOperationException e) {
      throw new LinkageError(null, e);
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

  public static <T> T defaultValue(Class<T> type) {
    checkNotNull(type);
    return (T) defaultValues.get(type);
  }

  private static final Map<Class<?>, Object> defaultValues = defaultValues();

  private static Map<Class<?>, Object> defaultValues() {
    Map<Class<?>, Object> map = new HashMap<>();
    map.put(boolean.class, false);
    map.put(char.class, '\0');
    map.put(byte.class, (byte) 0);
    map.put(short.class, (short) 0);
    map.put(int.class, 0);
    map.put(long.class, 0L);
    map.put(float.class, 0f);
    map.put(double.class, 0.0);
    return Collections.unmodifiableMap(map);
  }

  public static Class<?> tryWrap(Class<?> type) {
    checkNotNull(type);
    return wrapping.containsKey(type)
        ? wrapping.get(type)
        : type;
  }

  private static final Map<Class<?>, Class<?>> wrapping = wrapping();

  private static Map<Class<?>, Class<?>> wrapping() {
    Map<Class<?>, Class<?>> map = new HashMap<>();
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
