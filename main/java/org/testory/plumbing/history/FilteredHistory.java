package org.testory.plumbing.history;

import static org.testory.common.Chain.chain;
import static org.testory.plumbing.PlumbingException.check;

import org.testory.common.Chain;

public class FilteredHistory<T> {
  private final Class<T> type;
  private final History history;
  private Chain<Object> processed = chain();
  private Chain<T> filtered = chain();

  private FilteredHistory(Class<T> type, History history) {
    this.type = type;
    this.history = history;
  }

  public static <T> FilteredHistory<T> filter(Class<T> type, History history) {
    check(type != null);
    check(history != null);
    return new FilteredHistory<>(type, history);
  }

  public synchronized Chain<T> get() {
    update();
    return filtered;
  }

  private void update() {
    final Chain<Object> allEvents = history.get();

    Chain<Object> processing = allEvents;
    Chain<T> filtering = chain();
    while (processing.size() > 0) {
      if (processing == processed) {
        break;
      }
      Object event = processing.get();
      if (type.isInstance(event)) {
        filtering = filtering.add((T) event);
      }
      processing = processing.remove();
    }

    processed = allEvents;
    if (processing.size() == 0) {
      filtered = filtering.reverse();
    } else {
      filtered = filtered.addAll(filtering);
    }
  }
}
