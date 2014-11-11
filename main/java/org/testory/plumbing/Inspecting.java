package org.testory.plumbing;

import static org.testory.common.Collections.reverse;
import static org.testory.plumbing.PlumbingException.check;

import org.testory.common.Nullable;
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

  public static boolean hasInspecting(History history) {
    return tryLastInspecting(history) != null;
  }

  public static Inspecting lastInspecting(History history) {
    Inspecting inspecting = tryLastInspecting(history);
    check(inspecting != null);
    return inspecting;
  }

  @Nullable
  private static Inspecting tryLastInspecting(History history) {
    check(history != null);
    for (Object event : reverse(history.events)) {
      if (event instanceof Inspecting) {
        return (Inspecting) event;
      }
    }
    return null;
  }
}
