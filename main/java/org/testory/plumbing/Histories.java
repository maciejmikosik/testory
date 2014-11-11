package org.testory.plumbing;

import static org.testory.plumbing.History.history;
import static org.testory.plumbing.PlumbingException.check;

import java.util.ArrayList;
import java.util.List;

public class Histories {
  public static History log(Object event, History history) {
    check(event != null);
    check(history != null);
    List<Object> events = new ArrayList<Object>(history.events);
    events.add(event);
    return history(events);
  }
}
