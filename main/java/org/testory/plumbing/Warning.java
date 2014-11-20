package org.testory.plumbing;

import static org.testory.plumbing.PlumbingException.check;

import java.util.ArrayList;
import java.util.List;

public class Warning {
  public final String message;

  private Warning(String message) {
    this.message = message;
  }

  public static Warning warning(String message) {
    check(message != null);
    return new Warning(message);
  }

  public static List<Warning> findWarnings(History history) {
    check(history != null);
    List<Warning> warnings = new ArrayList<Warning>();
    for (Object event : history.events) {
      if (event instanceof Warning) {
        warnings.add((Warning) event);
      }
    }
    return warnings;
  }
}
