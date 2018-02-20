package org.testory.plumbing.mock;

import static org.testory.plumbing.PlumbingException.check;

import org.testory.proxy.Handler;
import org.testory.proxy.InvocationMatcher;

public class Stubbed {
  public final InvocationMatcher invocationMatcher;
  public final Handler handler;

  private Stubbed(InvocationMatcher invocationMatcher, Handler handler) {
    this.invocationMatcher = invocationMatcher;
    this.handler = handler;
  }

  public static Stubbed stubbed(InvocationMatcher invocationMatcher, Handler handler) {
    check(invocationMatcher != null);
    check(handler != null);
    return new Stubbed(invocationMatcher, handler);
  }

  public String toString() {
    return "stubbed(" + invocationMatcher + ", " + handler + ")";
  }
}
