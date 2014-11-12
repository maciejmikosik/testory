package org.testory.plumbing;

import static org.testory.plumbing.History.latest;
import static org.testory.plumbing.PlumbingException.check;

import org.testory.common.Optional;
import org.testory.util.Effect;

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

  public static Optional<Inspecting> findLastInspecting(History history) {
    check(history != null);
    for (Object event : latest(history)) {
      if (event instanceof Inspecting) {
        return Optional.of((Inspecting) event);
      }
    }
    return Optional.empty();
  }
}
