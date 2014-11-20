package org.testory.proxy;

public interface InvocationMatcher {
  boolean matches(Invocation invocation);
}
