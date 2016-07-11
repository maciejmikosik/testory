package org.testory.plumbing;

import static org.testory.plumbing.PlumbingException.check;

import org.testory.common.Effect;

public class Inspecting {
  public final Effect effect;

  private Inspecting(Effect effect) {
    this.effect = effect;
  }

  public static Inspecting inspecting(Effect effect) {
    check(effect != null);
    return new Inspecting(effect);
  }

  public String toString() {
    return "inspecting(" + effect + ")";
  }
}
