package org.testory.plumbing;

import static java.util.Arrays.asList;
import static org.testory.plumbing.History.add;
import static org.testory.plumbing.History.history;
import static org.testory.plumbing.PlumbingException.check;

import java.util.ArrayList;
import java.util.List;

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
    List<Object> events = unverified(history).events;
    for (int i = 0; i < events.size(); i++) {
      Object event = events.get(i);
      if (event instanceof Calling) {
        Calling calling = (Calling) event;
        if (invocationMatcher.matches(calling.invocation)) {
          History unverified = history(events.subList(i + 1, events.size()));
          return Optional.of(add(verifyingInOrder(unverified), history));
        }
      }
    }
    return Optional.empty();
  }

  private static History unverified(History history) {
    List<Object> events = new ArrayList<Object>(history.events);
    VerifyingInOrder guardian = verifyingInOrder(history(asList()));
    events.add(0, guardian);
    for (int i = events.size() - 1; i >= 0; i--) {
      if (events.get(i) instanceof VerifyingInOrder) {
        VerifyingInOrder event = (VerifyingInOrder) events.get(i);
        List<Object> unverifiedEvents = new ArrayList<Object>();
        unverifiedEvents.addAll(event.unverified.events);
        unverifiedEvents.addAll(events.subList(i + 1, events.size()));
        return history(unverifiedEvents);
      }
    }
    throw new Error();
  }
}
