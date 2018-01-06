package org.testory.plumbing.history;

import static org.testory.common.Chain.chain;
import static org.testory.plumbing.PlumbingException.check;

import org.testory.common.Chain;

public class RawHistory implements History {
  private Chain<Object> events = chain();

  private RawHistory() {}

  public static History newRawHistory() {
    return new RawHistory();
  }

  public Chain<Object> get() {
    return events;
  }

  public void add(Object event) {
    check(event != null);
    events = events.add(event);
  }

  public void cut(Chain<Object> tail) {
    Chain<Object> iterating = events;
    Chain<Object> iterated = chain();
    while (iterating != tail) {
      iterated = iterated.add(iterating.get());
      iterating = iterating.remove();
    }
    events = iterated.reverse();
  }
}
