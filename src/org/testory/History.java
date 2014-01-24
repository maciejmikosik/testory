package org.testory;

import static org.testory.common.Checks.checkNotNull;

import org.testory.util.Effect;

public class History {
  private final ThreadLocal<Effect> data = new ThreadLocal<Effect>();

  public History() {}

  public void logWhen(Effect effect) {
    checkNotNull(effect);
    data.set(effect);
  }

  public Effect getLastWhenEffect() {
    Effect effect = data.get();
    check(effect != null);
    return effect;
  }

  private static void check(boolean condition) {
    if (!condition) {
      throw new TestoryException();
    }
  }
}
