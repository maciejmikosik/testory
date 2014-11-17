package org.testory.common;

public interface DiagnosticMatcher extends Matcher {
  public String diagnose(@Nullable Object item);
}
