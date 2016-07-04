package org.testory.plumbing.history;

import static org.testory.common.Chain.chain;
import static org.testory.plumbing.PlumbingException.check;

import org.testory.common.Chain;
import org.testory.plumbing.Inspecting;

public class PurgedHistory implements History {
  private int numberOfInspections = 0;
  private Chain<Object> events = chain();

  private PurgedHistory() {}

  public static History newPurgedHistory() {
    return new PurgedHistory();
  }

  public Chain<Object> get() {
    return events;
  }

  // TODO remove from interface
  public void set(Chain<Object> events) {
    check(events != null);
    this.events = events;
  }

  public History add(Object event) {
    check(event != null);
    events = events.add(event);
    if (event instanceof Inspecting) {
      numberOfInspections++;
    }
    purge();
    return this;
  }

  /* 2 inspections happen for chained invocation of when */
  private void purge() {
    if (numberOfInspections > 2) {
      numberOfInspections = 0;
      Chain<Object> purging = chain();
      for (Object event : events) {
        if (event instanceof Inspecting) {
          if (numberOfInspections == 2) {
            break;
          }
          numberOfInspections++;
        }
        purging = purging.add(event);
      }
      events = purging.reverse();
    }
  }
}
