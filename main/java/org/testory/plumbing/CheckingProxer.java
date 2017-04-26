package org.testory.plumbing;

import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.Proxer;
import org.testory.proxy.Typing;

public class CheckingProxer implements Proxer {
  private final Proxer proxer;
  private final Checker checker;

  private CheckingProxer(Proxer proxer, Checker checker) {
    this.proxer = proxer;
    this.checker = checker;
  }

  public static Proxer checkingProxer(Checker checker, Proxer proxer) {
    return new CheckingProxer(proxer, checker);
  }

  public Object proxy(Typing typing, Handler handler) {
    return proxer.proxy(typing, mockHandler(handler));
  }

  private Handler mockHandler(final Handler handler) {
    return new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        checker.mustBeMock(invocation.instance);
        return handler.handle(invocation);
      }
    };
  }
}
