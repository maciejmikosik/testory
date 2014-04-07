package org.testory.common;

import static org.testory.common.CharSequences.join;
import static org.testory.common.Checks.checkNotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Matchers {
  public static Matcher arrayOf(List<Matcher> elementsMatchers) {
    for (Matcher matcher : elementsMatchers) {
      checkNotNull(matcher);
    }
    final List<Matcher> matchers = new ArrayList<Matcher>(elementsMatchers);
    return new Matcher() {
      public boolean matches(@Nullable Object item) {
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
}
