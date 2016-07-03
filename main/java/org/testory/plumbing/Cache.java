package org.testory.plumbing;

import static org.testory.common.Chain.chain;
import static org.testory.plumbing.PlumbingException.check;

import org.testory.common.Chain;

public class Cache {
  public final Class<?> type;
  public final Chain<Object> untilLastEvent;
  public final Chain<Object> untilLastTyped;

  private Cache(Class<?> type, Chain<Object> untilLastEvent, Chain<Object> untilLastTyped) {
    this.type = type;
    this.untilLastEvent = untilLastEvent;
    this.untilLastTyped = untilLastTyped;
  }

  public static Cache newCache(Class<?> type) {
    return new Cache(type, chain(), chain());
  }

  public Cache update(Chain<Object> history) {
    check(history != null);
    Chain<Object> events = history;
    while (events.size() > 0) {
      if (events == untilLastEvent) {
        return new Cache(type, history, untilLastTyped);
      }
      if (type.isInstance(events.get())) {
        return new Cache(type, history, events);
      }
      events = events.remove();
    }
    if (events.size() == 0) {
      return new Cache(type, history, history);
    }
    return this;
  }
}
