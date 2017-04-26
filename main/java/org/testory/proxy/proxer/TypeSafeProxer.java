package org.testory.proxy.proxer;

import static org.testory.common.Classes.canReturn;
import static org.testory.common.Classes.canThrow;
import static org.testory.proxy.ProxyException.check;

import java.lang.reflect.Method;

import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.Proxer;
import org.testory.proxy.Typing;

public class TypeSafeProxer implements Proxer {
  private final Proxer proxer;

  private TypeSafeProxer(Proxer proxer) {
    this.proxer = proxer;
  }

  public static Proxer typeSafe(Proxer proxer) {
    return new TypeSafeProxer(proxer);
  }

  public Object proxy(Typing typing, Handler handler) {
    check(typing != null);
    check(handler != null);
    return proxer.proxy(typing, typeSafe(handler));
  }

  private Handler typeSafe(final Handler handler) {
    return new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        Method method = invocation.method;
        Object returned;
        try {
          returned = handler.handle(invocation);
        } catch (Throwable throwable) {
          check(canThrow(throwable, method));
          throw throwable;
        }
        check(canReturn(returned, method) || method.getReturnType() == void.class && returned == null);
        return returned;
      }
    };
  }
}
