package org.testory.util;

import static org.testory.common.CharSequences.join;
import static org.testory.common.Checks.checkArgument;
import static org.testory.common.Throwables.gently;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.testory.common.Matcher;
import org.testory.common.Nullable;
import org.testory.proxy.Invocation;

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

  public static final Matcher anything = new Matcher() {
    public boolean matches(@Nullable Object item) {
      return true;
    }

    public String toString() {
      return "anything";
    }
  };

  public static Matcher invocationMatcher(final Method method, final Object instance,
      final List<Matcher> arguments) {
    return new Matcher() {
      public boolean matches(Object item) {
        Invocation invocation = (Invocation) item;
        return instance == invocation.instance && method.equals(invocation.method)
            && containsInOrder(arguments).matches(invocation.arguments);
      }

      public String toString() {
        return instance + "." + method.getName() + "(" + join(", ", arguments) + ")";
      }
    };
  }

  private static Matcher containsInOrder(final List<Matcher> elements) {
    return new Matcher() {
      public boolean matches(Object uncastItem) {
        List<?> item = (List<?>) uncastItem;
        if (item.size() != elements.size()) {
          return false;
        }
        for (int i = 0; i < elements.size(); i++) {
          if (!elements.get(i).matches(item.get(i))) {
            return false;
          }
        }
        return true;
      }
    };
  }
}
