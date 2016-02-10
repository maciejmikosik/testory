package org.testory.plumbing;

import static org.testory.plumbing.PlumbingException.check;

import org.testory.common.Chain;

public class History {
  public final Chain<Object> events;

  private History(Chain<Object> events) {
    this.events = events;
  }

  public static History history(Chain<Object> events) {
    check(events != null);
    return new History(events);
  }

  public static History add(Object event, History history) {
    check(event != null);
    check(history != null);
    return new History(history.events.add(event));
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (Object event : events) {
      builder.append(event).append('\n');
    }
    return builder.toString();
  }
}
