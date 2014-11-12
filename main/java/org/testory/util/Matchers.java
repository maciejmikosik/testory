package org.testory.util;

import static java.util.Arrays.asList;
import static org.testory.common.Checks.checkArgument;
import static org.testory.common.Classes.setAccessible;
import static org.testory.common.Throwables.gently;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.testory.common.Matcher;
import org.testory.common.Optional;

public class Matchers {
  public static boolean isMatcher(Object matcher) {
    return findMatchingMethod(matcher.getClass()).isPresent();
  }

  public static Matcher asMatcher(final Object matcher) {
    Optional<Method> optionalMethod = findMatchingMethod(matcher.getClass());
    checkArgument(optionalMethod.isPresent());
    final Method method = optionalMethod.get();
    setAccessible(method);
    return new Matcher() {
      public boolean matches(Object item) {
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

  private static Optional<Method> findMatchingMethod(Class<?> type) {
    for (String name : asList("matches", "apply")) {
      try {
        Method method = type.getMethod(name, Object.class);
        if (hasCorrectSignature(method)) {
          return Optional.of(method);
        }
      } catch (NoSuchMethodException e) {}
    }
    return Optional.empty();
  }

  private static boolean hasCorrectSignature(Method method) {
    return asList(boolean.class, Boolean.class).contains(method.getReturnType())
        && method.getExceptionTypes().length == 0;
  }
}
