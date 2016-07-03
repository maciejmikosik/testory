package org.testory.plumbing;

import static org.testory.common.Chain.chain;
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

  public static Chain<Object> purge(Chain<Object> history) {
    check(history != null);
    Chain<Object> purged = chain();
    for (Object event : history) {
      if (event instanceof Purging) {
        break;
      } else {
        purged = purged.add(event);
      }
    }
    return purged.reverse();
  }

  public static Chain<Object> mark(Chain<Object> history) {
    check(history != null);
    return history.add(purging());
  }
}
