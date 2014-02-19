package org.testory.common;

import static org.testory.common.Checks.checkArgument;
import static org.testory.common.Checks.checkNotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Classes {
  // TODO move tests from Invocation to Classes
  public static boolean isAssignableTo(Class<?> type, @Nullable Object instance) {
    checkNotNull(type);
    return type.isPrimitive()
        ? isConvertibleTo(type, instance)
        : instance == null || type.isAssignableFrom(instance.getClass());
  }

  private static boolean isConvertibleTo(Class<?> type, Object instance) {
    checkArgument(type.isPrimitive());
    if (type == void.class) {
      return false;
    }
    try {
      Method method = PrimitiveMethods.class.getDeclaredMethod("method", type);
      method.setAccessible(true);
      method.invoke(null, instance);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    } catch (NoSuchMethodException e) {
      throw new Error(e);
    } catch (IllegalAccessException e) {
      throw new Error(e);
    } catch (InvocationTargetException e) {
      throw new Error(e);
    }
  }

  @SuppressWarnings("unused")
  private static class PrimitiveMethods {
    private static void method(byte argument) {}

    private static void method(short argument) {}

    private static void method(int argument) {}

    private static void method(long argument) {}

    private static void method(float argument) {}

    private static void method(double argument) {}

    private static void method(boolean argument) {}

    private static void method(char argument) {}
  }

  // TODO write tests
  public static boolean couldReturn(@Nullable Object object, Method method) {
    checkNotNull(method);
    return canReturn(object, method) || object == null;
  }

  // TODO write tests
  public static boolean canReturn(@Nullable Object object, Method method) {
    checkNotNull(method);
    return isAssignableTo(method.getReturnType(), object);
  }

  // TODO write tests
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
}
