package org.testory.common;

import static org.testory.common.CharSequences.join;
import static org.testory.common.Checks.checkNotNull;
import static org.testory.common.Objects.areEqualDeep;

import java.lang.reflect.Array;
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

  public static class ProxyMatcher implements Matcher {
    private final Matcher target;

    public ProxyMatcher(Matcher target) {
      this.target = checkNotNull(target);
    }

    public boolean matches(Object item) {
      return target.matches(item);
    }

    public String toString() {
      return target.toString();
    }
  }
}
