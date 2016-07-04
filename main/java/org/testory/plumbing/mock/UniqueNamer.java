package org.testory.plumbing.mock;

import static org.testory.plumbing.PlumbingException.check;
import static org.testory.plumbing.Purging.purge;

import java.util.ArrayList;
import java.util.List;

import org.testory.plumbing.History;
import org.testory.plumbing.Mocking;

public class UniqueNamer implements Namer {
  private final History history;

  private UniqueNamer(History history) {
    this.history = history;
  }

  public static Namer uniqueNamer(History history) {
    check(history != null);
    return new UniqueNamer(history);
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
    for (Object event : purge(history.get())) {
      if (event instanceof Mocking) {
        usedNames.add(((Mocking) event).name);
      }
    }
    return usedNames;
  }
}
