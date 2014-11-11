package org.testory.plumbing;

import static org.testory.plumbing.PlumbingException.check;

import java.util.ArrayList;
import java.util.List;

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
}
