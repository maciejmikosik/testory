package org.testory;

import org.testory.proxy.Invocation;

public interface InvocationMatcher {
  boolean matches(Invocation invocation);
}
