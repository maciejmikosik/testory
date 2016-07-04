package org.testory.plumbing;

import static org.testory.plumbing.PlumbingException.check;

import org.testory.proxy.Handler;
import org.testory.proxy.InvocationMatcher;

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
}
