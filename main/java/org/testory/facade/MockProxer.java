package org.testory.facade;

import org.testory.TestoryException;
import org.testory.plumbing.Checker;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.Proxer;
import org.testory.proxy.Typing;

public class MockProxer implements Proxer {
  private final Proxer proxer;
  private final Checker checker;

  private MockProxer(Proxer proxer, Checker checker) {
    this.proxer = proxer;
    this.checker = checker;
  }

  public static Proxer mockProxer(Proxer proxer, Checker checker) {
    return new MockProxer(proxer, checker);
  }

  public Object proxy(Typing typing, Handler handler) {
    Handler mockHandler = mockHandler(handler);
    try {
      return proxer.proxy(typing, mockHandler);
    } catch (RuntimeException e) {
      throw new TestoryException(e);
    }
  }

  private Handler mockHandler(final Handler handler) {
    return new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        checker.mustBeMock(invocation.instance);
        Object returned;
        try {
          returned = handler.handle(invocation);
        } catch (Throwable throwable) {
          checker.canThrow(throwable, invocation.method);
          throw throwable;
        }
        checker.canReturn(returned, invocation.method);
        return returned;
      }
    };
  }
}
