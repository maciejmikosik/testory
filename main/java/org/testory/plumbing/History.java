package org.testory.plumbing;

import static org.testory.common.Chain.chain;
import static org.testory.plumbing.PlumbingException.check;

import org.testory.common.Chain;

public class History {
  private Chain<Object> events = chain();

  public Chain<Object> get() {
    return events;
  }

  public void set(Chain<Object> events) {
    check(events != null);
    this.events = events;
  }

  public History add(Object event) {
    check(event != null);
    events = events.add(event);
    return this;
  }
}
