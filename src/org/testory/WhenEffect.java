package org.testory;

import org.testory.util.Effect;

public class WhenEffect {
  public static final ThreadLocal<Effect> whenEffect = new ThreadLocal<Effect>();
}
