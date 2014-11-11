package org.testory.plumbing;

import static org.testory.plumbing.History.add;
import static org.testory.plumbing.History.latest;
import static org.testory.plumbing.PlumbingException.check;

import java.util.ArrayList;
import java.util.List;

import org.testory.util.any.Any;

public class Capturing {
  public static class CapturingAny {
    public final Any any;

    private CapturingAny(Any any) {
      this.any = any;
    }

    public static CapturingAny capturingAny(Any any) {
      check(any != null);
      return new CapturingAny(any);
    }

    public String toString() {
      return "capturingAny(" + any + ")";
    }
  }

  public static class ConsumingAnys {
    public String toString() {
      return "consumingAnys()";
    }
  }

  public static List<Any> capturedAnys(History history) {
    check(history != null);
    List<Any> anys = new ArrayList<Any>();
    for (Object event : latest(history)) {
      if (event instanceof CapturingAny) {
        anys.add(0, ((CapturingAny) event).any);
      } else if (event instanceof ConsumingAnys) {
        break;
      }
    }
    return anys;
  }

  public static History consumeAnys(History history) {
    return add(new ConsumingAnys(), history);
  }
}
