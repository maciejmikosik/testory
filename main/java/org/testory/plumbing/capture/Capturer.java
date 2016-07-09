package org.testory.plumbing.capture;

import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public interface Capturer {
  InvocationMatcher capture(Invocation invocation);
}
