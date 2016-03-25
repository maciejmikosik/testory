package org.testory;

import static org.testory.TestoryException.check;
import static org.testory.common.Classes.canReturn;
import static org.testory.common.Classes.canThrow;

import java.lang.reflect.Method;

import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.Proxer;
import org.testory.proxy.Typing;

public class TestoryProxer implements Proxer {
  private final Proxer proxer;

  public TestoryProxer(Proxer proxer) {
    this.proxer = proxer;
  }

  public Object proxy(Typing typing, Handler handler) {
    Handler compatibleHandler = compatible(handler);
    try {
      return proxer.proxy(typing, compatibleHandler);
    } catch (RuntimeException e) {
      throw new TestoryException(e);
    }
  }

  private static Handler compatible(final Handler handler) {
    return new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
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
}
