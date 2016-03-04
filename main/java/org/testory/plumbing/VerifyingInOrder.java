package org.testory.plumbing;

import static org.testory.common.Chain.chain;
import static org.testory.plumbing.History.history;
import static org.testory.plumbing.PlumbingException.check;

import org.testory.common.Chain;
import org.testory.common.Optional;
import org.testory.proxy.InvocationMatcher;

public class VerifyingInOrder {
  public final History unverified;

  private VerifyingInOrder(History unverified) {
    this.unverified = unverified;
  }

  public static VerifyingInOrder verifyingInOrder(History unverified) {
    check(unverified != null);
    return new VerifyingInOrder(unverified);
  }

  public static Optional<History> verifyInOrder(InvocationMatcher invocationMatcher, History history) {
    Chain<Object> unverified = unverifiedReversed(history);
    while (unverified.size() > 0) {
      Object event = unverified.get();
      unverified = unverified.remove();
      if (event instanceof Calling && invocationMatcher.matches(((Calling) event).invocation)) {
        VerifyingInOrder verifyingInOrder = verifyingInOrder(history(unverified.reverse()));
        return Optional.of(history(history.events.add(verifyingInOrder)));
      }
    }
    return Optional.empty();
  }

  private static Chain<Object> unverifiedReversed(History history) {
    Chain<Object> unverified = chain();
    for (Object event : history.events) {
      if (event instanceof VerifyingInOrder) {
        for (Object oldEvent : ((VerifyingInOrder) event).unverified.events) {
          unverified = unverified.add(oldEvent);
        }
        break;
      } else {
        unverified = unverified.add(event);
      }
    }
    return unverified;
  }
}
