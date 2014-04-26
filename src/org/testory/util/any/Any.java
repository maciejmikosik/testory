package org.testory.util.any;

import static org.testory.common.Checks.checkNotNull;
import static org.testory.common.Classes.tryWrap;
import static org.testory.util.Uniques.unique;

import org.testory.common.Matcher;
import org.testory.common.Matchers;

public class Any {
  public final Matcher matcher;
  public final Object token;

  private Any(Matcher matcher, Object token) {
    this.matcher = matcher;
    this.token = token;
  }

  public static Any any(final Class<?> type, Matcher matcher) {
    checkNotNull(type);
    checkNotNull(matcher);
    return new Any(matcher, unique(tryWrap(type))) {
      public String toString() {
        return "any(" + type.getName() + ", " + matcher + ")";
      }
    };
  }

  public static Any any(final Class<?> type) {
    checkNotNull(type);
    return new Any(Matchers.anything, unique(tryWrap(type))) {
      public String toString() {
        return "any(" + type.getName() + ")";
      }
    };
  }
}
