package org.testory.plumbing.verify;

import static org.testory.plumbing.PlumbingException.check;

import org.testory.common.Chain;

public class Verified {
  public final Chain<Object> remaining;

  private Verified(Chain<Object> remaining) {
    this.remaining = remaining;
  }

  public static Verified verified(Chain<Object> remaining) {
    check(remaining != null);
    return new Verified(remaining);
  }
}
