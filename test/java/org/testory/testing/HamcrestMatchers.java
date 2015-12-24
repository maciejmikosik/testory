package org.testory.testing;

import static java.text.MessageFormat.format;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class HamcrestMatchers {
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

  public static <T> Matcher<T> hamcrestDiagnosticMatcher() {
    return new BaseMatcher<T>() {
      public boolean matches(Object item) {
        return false;
      }

      public void describeMismatch(Object item, Description description) {
        description.appendText(diagnosed(item));
      }

      public void describeTo(Description description) {
        description.appendText("hamcrestDiagnosticMatcher.toString()");
      }
    };
  }

  public static String diagnosed(Object item) {
    return format("diagnosed({0})", item);
  }
}
