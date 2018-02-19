package org.testory.plumbing;

import static org.testory.common.Chain.chain;
import static org.testory.plumbing.PlumbingException.check;

import org.testory.common.Chain;
import org.testory.common.Optional;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class VerifyingInOrder {
  public final Chain<Object> unverified;

  private VerifyingInOrder(Chain<Object> unverified) {
    this.unverified = unverified;
  }

  public static VerifyingInOrder verifyingInOrder(Chain<Object> unverified) {
    check(unverified != null);
    return new VerifyingInOrder(unverified);
  }

  public static Optional<VerifyingInOrder> verifyInOrder(InvocationMatcher invocationMatcher, Chain<Object> history) {
    Chain<Object> unverified = unverifiedReversed(history);
    while (unverified.size() > 0) {
      Object event = unverified.get();
      unverified = unverified.remove();
      if (event instanceof Invocation && invocationMatcher.matches((Invocation) event)) {
        return Optional.of(verifyingInOrder(unverified.reverse()));
      }
    }
    return Optional.empty();
  }

  private static Chain<Object> unverifiedReversed(Chain<Object> history) {
    Chain<Object> unverified = chain();
    for (Object event : history) {
      if (event instanceof VerifyingInOrder) {
        unverified = unverified.addAll(((VerifyingInOrder) event).unverified);
        break;
      } else {
        unverified = unverified.add(event);
      }
    }
    return unverified;
  }
}
