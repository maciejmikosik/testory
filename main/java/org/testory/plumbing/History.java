package org.testory.plumbing;

import static java.util.Collections.unmodifiableList;
import static org.testory.plumbing.PlumbingException.check;

import java.util.ArrayList;
import java.util.List;

public class History {
  public final List<Object> events;

  private History(List<Object> events) {
    this.events = events;
  }

  public static History history(List<Object> events) {
    check(events != null);
    return verify(new History(unmodifiableList(new ArrayList<Object>(events))));
  }

  private static History verify(History history) {
    for (Object event : history.events) {
      check(event != null);
    }
    return history;
  }
}
