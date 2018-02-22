package org.testory.common;

import static java.util.Objects.requireNonNull;

public class DelegatingMatcher implements Matcher {
  private final Matcher matcher;

  public DelegatingMatcher(Matcher matcher) {
    this.matcher = requireNonNull(matcher);
  }

  public boolean matches(Object item) {
    return matcher.matches(item);
  }

  public String toString() {
    return matcher.toString();
  }
}
