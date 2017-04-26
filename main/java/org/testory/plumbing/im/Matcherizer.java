package org.testory.plumbing.im;

import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public interface Matcherizer {
  InvocationMatcher matcherize(Invocation invocation);
}
