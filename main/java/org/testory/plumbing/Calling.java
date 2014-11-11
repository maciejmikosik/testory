package org.testory.plumbing;

import static org.testory.plumbing.PlumbingException.check;

import java.util.ArrayList;
import java.util.List;

import org.testory.proxy.Invocation;

public class Calling {
  public final Invocation invocation;

  private Calling(Invocation invocation) {
    this.invocation = invocation;
  }

  public static Calling calling(Invocation invocation) {
    check(invocation != null);
    return new Calling(invocation);
  }

  public String toString() {
    return "calling(" + invocation + ")";
  }

  public static List<Calling> callings(History history) {
    List<Calling> callings = new ArrayList<Calling>();
    for (Object event : history.events) {
      if (event instanceof Calling) {
        callings.add((Calling) event);
      }
    }
    return callings;
  }
}
