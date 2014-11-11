package org.testory.plumbing;

import static org.testory.common.Collections.reverse;
import static org.testory.plumbing.PlumbingException.check;

import java.util.ArrayList;
import java.util.List;

import org.testory.util.any.Any;

public class History {
  List<Object> events = new ArrayList<Object>();

  public History() {}

  private History(List<Object> events) {
    this.events = events;
  }

  public static History history(List<Object> events) {
    check(events != null);
    return new History(events);
  }

  private List<Object> getEvents() {
    return new ArrayList<Object>(events);
  }

  private void addEvent(Object event) {
    events.add(event);
  }

  public void logAny(Any any) {
    addEvent(any);
  }

  public List<Any> getAnysAndConsume() {
    class Consumer {}
    List<Any> anys = new ArrayList<Any>();
    for (Object event : reverse(getEvents())) {
      if (event instanceof Any) {
        anys.add(0, (Any) event);
      } else if (event instanceof Consumer) {
        break;
      }
    }
    addEvent(new Consumer());
    return anys;
  }
}
