package org.testory.plumbing.mock;

import static org.testory.plumbing.PlumbingException.check;
import static org.testory.plumbing.history.FilteredHistory.filter;

import java.util.ArrayList;
import java.util.List;

import org.testory.plumbing.Mocking;
import org.testory.plumbing.history.FilteredHistory;
import org.testory.plumbing.history.History;

public class UniqueNamer implements Namer {
  private final FilteredHistory<Mocking> mockingHistory;

  private UniqueNamer(FilteredHistory<Mocking> mockingHistory) {
    this.mockingHistory = mockingHistory;
  }

  public static Namer uniqueNamer(History history) {
    check(history != null);
    return new UniqueNamer(filter(Mocking.class, history));
  }

  public String name(Class<?> type) {
    check(type != null);
    List<String> usedNames = usedNames();
    for (int i = 0;; i++) {
      String name = "mock" + type.getSimpleName() + i;
      if (!usedNames.contains(name)) {
        return name;
      }
    }
  }

  private List<String> usedNames() {
    List<String> usedNames = new ArrayList<String>();
    for (Mocking mocking : mockingHistory.get()) {
      usedNames.add(mocking.name);
    }
    return usedNames;
  }
}
