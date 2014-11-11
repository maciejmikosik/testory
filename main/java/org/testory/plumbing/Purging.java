package org.testory.plumbing;

import static org.testory.common.Collections.reverse;
import static org.testory.plumbing.History.history;
import static org.testory.plumbing.PlumbingException.check;

import java.util.ArrayList;
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
    List<Object> events = new ArrayList<Object>(history.events);
    List<Object> latestEvents = reverse(events);
    for (int i = 0; i < latestEvents.size(); i++) {
      if (latestEvents.get(i) instanceof Purging) {
        events = reverse(latestEvents.subList(0, i));
        break;
      }
    }
    events.add(purging());
    return history(events);
  }

  public static History purgeMark(History history) {
    check(history != null);
    List<Object> events = new ArrayList<Object>(history.events);
    events.add(purging());
    return history(events);
  }

  public static History purgeNow(History history) {
    check(history != null);
    return history(new ArrayList<Object>());
  }
}
