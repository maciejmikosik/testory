package org.testory.plumbing;

import static org.testory.plumbing.History.add;
import static org.testory.plumbing.History.history;
import static org.testory.plumbing.PlumbingException.check;

import java.util.List;

public class Purging {
  private Purging() {}

  public static Purging purging() {
    return new Purging();
  }

  public String toString() {
    return "purging()";
  }

  public static History purge(History history) {
    check(history != null);
    List<Object> events = history.events;
    for (int i = events.size() - 1; i >= 0; i--) {
      if (events.get(i) instanceof Purging) {
        return history(events.subList(i + 1, events.size()));
      }
    }
    return history;
  }

  public static History mark(History history) {
    check(history != null);
    return add(purging(), history);
  }
}
