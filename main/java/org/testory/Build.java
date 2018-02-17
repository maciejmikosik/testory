package org.testory;

import static java.util.Arrays.asList;
import static org.testory.common.Collections.immutable;

import java.util.HashSet;
import java.util.Set;

class Build {
  @SuppressWarnings("deprecation")
  public static Set<Class<?>> exposed = immutable(new HashSet<>(asList(
      org.testory.Testory.class,
      org.testory.TestoryException.class,
      org.testory.TestoryAssertionError.class,
      org.testory.common.Closure.class,
      org.testory.Closure.class,
      org.testory.common.VoidClosure.class,
      org.testory.common.Nullable.class,
      org.testory.proxy.Invocation.class,
      org.testory.proxy.Handler.class,
      org.testory.proxy.InvocationMatcher.class,
      org.testory.proxy.ProxyException.class)));
}
