package org.testory;

import org.testory.common.Closure;

public class WhenEffect {
  public static final ThreadLocal<Closure> whenEffect = new ThreadLocal<Closure>();
}
