package org.testory.util;

import static org.testory.common.Checks.checkArgument;
import static org.testory.common.Throwables.gently;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.testory.common.Matcher;
import org.testory.common.Nullable;

public class Matchers {
  public static boolean isMatcher(Object matcher) {
    return tryGetMatcherMethod(matcher.getClass()) != null;
  }

  public static Matcher asMatcher(final Object matcher) {
    final Method method = tryGetMatcherMethod(matcher.getClass());
    checkArgument(method != null);
    method.setAccessible(true);
    return new Matcher() {
      public boolean matches(@Nullable Object item) {
        try {
          return (Boolean) method.invoke(matcher, item);
        } catch (IllegalArgumentException e) {
          throw new Error(e);
        } catch (IllegalAccessException e) {
          throw new Error(e);
        } catch (InvocationTargetException e) {
          // matcher method does not throw any checked exceptions
          throw gently(e.getCause());
        }
      }

      public String toString() {
        return matcher.toString();
      }
    };
  }

  @Nullable
  private static Method tryGetMatcherMethod(Class<?> type) {
    Method method = tryGetMatcherMethod(type, "matches");
    return method != null
        ? method
        : tryGetMatcherMethod(type, "apply");
  }

  @Nullable
  private static Method tryGetMatcherMethod(Class<?> type, String name) {
    try {
      Method method = type.getMethod(name, Object.class);
      Class<?> returnType = method.getReturnType();
      boolean hasCorrectSignature = (returnType == boolean.class || returnType == Boolean.class)
          && method.getExceptionTypes().length == 0;
      return hasCorrectSignature
          ? method
          : null;
    } catch (NoSuchMethodException e) {
      return null;
    }
  }
}
