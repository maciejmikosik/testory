package org.testory;

import static org.testory.TestoryException.check;
import static org.testory.common.Classes.canReturn;
import static org.testory.common.Classes.canThrow;
import static org.testory.plumbing.FilteredHistory.filter;

import java.lang.reflect.Method;

import org.testory.plumbing.FilteredHistory;
import org.testory.plumbing.History;
import org.testory.plumbing.Mocking;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.Proxer;
import org.testory.proxy.Typing;

public class MockProxer implements Proxer {
  private final Proxer proxer;
  private final FilteredHistory<Mocking> mockingHistory;

  private MockProxer(Proxer proxer, FilteredHistory<Mocking> mockingHistory) {
    this.proxer = proxer;
    this.mockingHistory = mockingHistory;
  }

  public static Proxer mockProxer(History history, Proxer proxer) {
    return new MockProxer(proxer, filter(Mocking.class, history));
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
        check(isMock(invocation.instance));
        Object returned;
        try {
          returned = handler.handle(invocation);
        } catch (Throwable throwable) {
          check(canThrow(throwable, invocation.method));
          throw throwable;
        }
        check(canReturn(returned, invocation.method) || canReturnVoid(returned, invocation.method));
        return returned;
      }

      private boolean canReturnVoid(Object returned, Method method) {
        return method.getReturnType() == void.class && returned == null;
      }
    };
  }

  private boolean isMock(Object instance) {
    for (Mocking mocking : mockingHistory.get()) {
      if (mocking.mock == instance) {
        return true;
      }
    }
    return false;
  }
}
