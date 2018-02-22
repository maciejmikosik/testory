package org.testory.plumbing.mock;

import static java.lang.String.format;
import static org.testory.plumbing.PlumbingException.check;
import static org.testory.plumbing.history.FilteredHistory.filter;

import java.util.ArrayList;
import java.util.List;

import org.testory.plumbing.history.FilteredHistory;
import org.testory.plumbing.history.History;

public class UniqueNamer implements Namer {
  private final FilteredHistory<Mocked> mockedHistory;

  private UniqueNamer(FilteredHistory<Mocked> mockedHistory) {
    this.mockedHistory = mockedHistory;
  }

  public static Namer uniqueNamer(History history) {
    check(history != null);
    return new UniqueNamer(filter(Mocked.class, history));
  }

  public String name(Class<?> type) {
    check(type != null);
    List<String> usedNames = usedNames();
    for (int i = 0;; i++) {
      String name = format("mock%s%s", type.getSimpleName(), i);
      if (!usedNames.contains(name)) {
        return name;
      }
    }
  }

  private List<String> usedNames() {
    List<String> usedNames = new ArrayList<>();
    for (Mocked mocked : mockedHistory.get()) {
      usedNames.add(mocked.name);
    }
    return usedNames;
  }
}
