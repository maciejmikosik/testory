package org.testory.proxy.proxer;

import static org.testory.common.Classes.defaultValue;
import static org.testory.proxy.ProxyException.check;

import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.Proxer;
import org.testory.proxy.Typing;

/**
 *
 */
public class PrimitiveFixerProxer implements Proxer {
  private final Proxer proxer;

  private PrimitiveFixerProxer(Proxer proxer) {
    this.proxer = proxer;
  }

  public static Proxer primitiveFixer(Proxer proxer) {
    check(proxer != null);
    return new PrimitiveFixerProxer(proxer);
  }

  public Object proxy(Typing typing, Handler handler) {
    check(typing != null);
    check(handler != null);
    return proxer.proxy(typing, fix(handler));
  }

  private static Handler fix(final Handler handler) {
    return new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        Class<?> returnType = invocation.method.getReturnType();
        Object result = handler.handle(invocation);
        return returnType.isPrimitive() && result == null
            ? defaultValue(returnType)
            : result;
      }
    };
  }

  public static class ProxiableObject {}
}
