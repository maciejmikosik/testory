package org.testory.testing;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class Matchers {
  public static Matcher<Throwable> hasMessageContaining(final String substring) {
    return new TypeSafeMatcher<Throwable>() {
      protected boolean matchesSafely(Throwable throwable) {
        return throwable.getMessage().contains(substring);
      }

      public void describeTo(Description description) {
        description.appendText("hasMessageContaining(" + substring + ")");
      }
    };
  }

  public static Matcher<Throwable> hasMessageMatching(final String regex) {
    return new TypeSafeMatcher<Throwable>() {
      protected boolean matchesSafely(Throwable throwable) {
        return throwable.getMessage().matches(regex);
      }

      public void describeTo(Description description) {
        description.appendText("hasMessageMatching(" + regex + ")");
      }
    };
  }
}
