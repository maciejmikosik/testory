package org.testory.common;

import static org.testory.common.Checks.checkNotNull;

public class DelegatingMatcher implements Matcher {
  private final Matcher matcher;

  public DelegatingMatcher(Matcher matcher) {
    this.matcher = checkNotNull(matcher);
  }

  public boolean matches(Object item) {
    return matcher.matches(item);
  }

  public String toString() {
    return matcher.toString();
  }
}
