package org.testory.plumbing;

import static org.testory.common.Chain.chain;
import static org.testory.plumbing.History.add;
import static org.testory.plumbing.History.history;
import static org.testory.plumbing.PlumbingException.check;

import org.testory.common.Chain;

public class Purging {
  private Purging() {}

  public static Purging purging() {
    return new Purging();
  }

  public String toString() {
    return "purging()";
  }

  public static History purge(History history) {
    check(history != null);
    Chain<Object> purged = chain();
    for (Object event : history.events) {
      if (event instanceof Purging) {
        break;
      } else {
        purged = purged.add(event);
      }
    }
    return history(purged.reverse());
  }

  public static History mark(History history) {
    check(history != null);
    return add(purging(), history);
  }
}
