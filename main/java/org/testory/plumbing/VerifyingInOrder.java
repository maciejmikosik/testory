package org.testory.plumbing;

import static org.testory.plumbing.PlumbingException.check;

import org.testory.common.Chain;

public class VerifyingInOrder {
  public final Chain<Object> unverified;

  private VerifyingInOrder(Chain<Object> unverified) {
    this.unverified = unverified;
  }

  public static VerifyingInOrder verifyingInOrder(Chain<Object> unverified) {
    check(unverified != null);
    return new VerifyingInOrder(unverified);
  }
}
