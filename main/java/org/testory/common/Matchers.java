package org.testory.common;

import static java.util.Arrays.asList;
import static java.util.Objects.deepEquals;
import static org.testory.common.CharSequences.join;
import static org.testory.common.Checks.checkArgument;
import static org.testory.common.Checks.checkNotNull;
import static org.testory.common.Classes.setAccessible;
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
        return deepEquals(object, item);
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
    return findMatchesMethod(matcher.getClass()).isPresent();
  }

  public static Matcher asMatcher(final Object matcher) {
    Optional<Method> matchesMethod = findMatchesMethod(matcher.getClass());
    checkArgument(matchesMethod.isPresent());
    Optional<Method> diagnoseMethod = findDiagnoseMethod(matcher.getClass());
    return diagnoseMethod.isPresent()
        ? newDiagnosticMatcher(matcher, matchesMethod.get(), diagnoseMethod.get())
        : newMatcher(matcher, matchesMethod.get());
  }

  private static Matcher newMatcher(final Object dynamicMatcher, final Method matchesMethod) {
    return new Matcher() {
      public boolean matches(Object item) {
        try {
          setAccessible(matchesMethod);
          return (Boolean) matchesMethod.invoke(dynamicMatcher, item);
        } catch (InvocationTargetException e) {
          throw gently(e.getCause());
        } catch (ReflectiveOperationException e) {
          throw new LinkageError(null, e);
        }
      }

      public String toString() {
        return dynamicMatcher.toString();
      }
    };
  }

  private static DiagnosticMatcher newDiagnosticMatcher(final Object dynamicMatcher,
      final Method matchesMethod, final Method diagnoseMethod) {
    final Matcher matcher = newMatcher(dynamicMatcher, matchesMethod);
    return new DiagnosticMatcher() {
      public boolean matches(Object item) {
        return matcher.matches(item);
      }

      public String toString() {
        return matcher.toString();
      }

      public String diagnose(@Nullable Object item) {
        try {
          Object description = Class.forName("org.hamcrest.StringDescription").newInstance();
          setAccessible(diagnoseMethod);
          diagnoseMethod.invoke(dynamicMatcher, item, description);
          return description.toString();
        } catch (InvocationTargetException e) {
          throw gently(e.getCause());
        } catch (ReflectiveOperationException e) {
          throw new LinkageError(null, e);
        }
      }
    };
  }

  private static Optional<Method> findMatchesMethod(Class<?> type) {
    for (String name : asList("matches", "apply")) {
      try {
        Method method = type.getMethod(name, Object.class);
        Class<?> returnType = method.getReturnType();
        if ((returnType == boolean.class || returnType == Boolean.class)
            && method.getExceptionTypes().length == 0) {
          return Optional.of(method);
        }
      } catch (NoSuchMethodException e) {}
    }
    return Optional.empty();
  }

  private static Optional<Method> findDiagnoseMethod(Class<?> type) {
    for (Method method : type.getMethods()) {
      Class<?>[] parameters = method.getParameterTypes();
      if (method.getName().equals("describeMismatch") && parameters.length == 2
          && parameters[0] == Object.class
          && parameters[1].getName().equals("org.hamcrest.Description")
          && method.getExceptionTypes().length == 0) {
        return Optional.of(method);
      }
    }
    return Optional.empty();
  }
}
