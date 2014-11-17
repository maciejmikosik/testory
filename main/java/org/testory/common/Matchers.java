package org.testory.common;

import static java.util.Arrays.asList;
import static org.testory.common.CharSequences.join;
import static org.testory.common.Checks.checkArgument;
import static org.testory.common.Checks.checkNotNull;
import static org.testory.common.Classes.setAccessible;
import static org.testory.common.Objects.areEqualDeep;
import static org.testory.common.Throwables.gently;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Matchers {
  public static final Matcher anything = new Matcher() {
    public boolean matches(Object item) {
      return true;
    }

    public String toString() {
      return "anything";
    }
  };

  public static Matcher same(@Nullable final Object object) {
    return new Matcher() {
      public boolean matches(Object item) {
        return object == item;
      }

      public String toString() {
        return "same(" + object + ")";
      }
    };
  }

  public static Matcher equalDeep(@Nullable final Object object) {
    return new Matcher() {
      public boolean matches(Object item) {
        return areEqualDeep(object, item);
      }

      public String toString() {
        return "equalDeep(" + object + ")";
      }
    };
  }

  public static Matcher arrayOf(List<Matcher> elementsMatchers) {
    for (Matcher matcher : elementsMatchers) {
      checkNotNull(matcher);
    }
    final List<Matcher> matchers = new ArrayList<Matcher>(elementsMatchers);
    return new Matcher() {
      public boolean matches(Object item) {
        return item != null && item.getClass().isArray()
            && matchers.size() == Array.getLength(item) && matchesElements(item);
      }

      private boolean matchesElements(Object item) {
        for (int i = 0; i < matchers.size(); i++) {
          if (!matchers.get(i).matches(Array.get(item, i))) {
            return false;
          }
        }
        return true;
      }

      public String toString() {
        return "arrayOf(" + join(", ", matchers) + ")";
      }
    };
  }

  public static Matcher listOf(List<Matcher> elementsMatchers) {
    for (Matcher matcher : elementsMatchers) {
      checkNotNull(matcher);
    }
    final List<Matcher> matchers = new ArrayList<Matcher>(elementsMatchers);
    return new Matcher() {
      public boolean matches(Object item) {
        return item instanceof List<?> && matchers.size() == ((List<?>) item).size()
            && matchesElements((List<?>) item);
      }

      private boolean matchesElements(List<?> list) {
        for (int i = 0; i < matchers.size(); i++) {
          if (!matchers.get(i).matches(list.get(i))) {
            return false;
          }
        }
        return true;
      }

      public String toString() {
        return "listOf(" + join(", ", matchers) + ")";
      }
    };
  }

  public static class MatcherDecorator implements Matcher {
    private final Matcher decorated;

    public MatcherDecorator(Matcher decorated) {
      this.decorated = checkNotNull(decorated);
    }

    public boolean matches(Object item) {
      return decorated.matches(item);
    }

    public String toString() {
      return decorated.toString();
    }
  }

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
