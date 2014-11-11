package org.testory.plumbing;

import static org.testory.plumbing.History.latest;
import static org.testory.plumbing.PlumbingException.check;

import org.testory.InvocationMatcher;
import org.testory.common.Nullable;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;

public class Stubbing {
  public final InvocationMatcher invocationMatcher;
  public final Handler handler;

  private Stubbing(InvocationMatcher invocationMatcher, Handler handler) {
    this.invocationMatcher = invocationMatcher;
    this.handler = handler;
  }

  public static Stubbing stubbing(InvocationMatcher invocationMatcher, Handler handler) {
    check(invocationMatcher != null);
    check(handler != null);
    return new Stubbing(invocationMatcher, handler);
  }

  public String toString() {
    return "stubbing(" + invocationMatcher + ", " + handler + ")";
  }

  public static boolean hasStubbing(Invocation invocation, History history) {
    return tryFindStubbing(invocation, history) != null;
  }

  public static Stubbing findStubbing(Invocation invocation, History history) {
    Stubbing stubbing = tryFindStubbing(invocation, history);
    check(stubbing != null);
    return stubbing;
  }

  @Nullable
  private static Stubbing tryFindStubbing(Invocation invocation, History history) {
    for (Object event : latest(history)) {
      if (event instanceof Stubbing) {
        Stubbing stubbing = (Stubbing) event;
        if (stubbing.invocationMatcher.matches(invocation)) {
          return stubbing;
        }
      }
    }
    return null;
  }
}
