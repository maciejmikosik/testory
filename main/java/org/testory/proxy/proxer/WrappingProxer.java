package org.testory.proxy.proxer;

import static org.testory.proxy.ProxyException.check;

import java.lang.reflect.Constructor;

import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.Proxer;
import org.testory.proxy.ProxyException;
import org.testory.proxy.Typing;

public class WrappingProxer implements Proxer {
  private final Constructor<? extends RuntimeException> constructor;
  private final Proxer proxer;

  private WrappingProxer(Constructor<? extends RuntimeException> constructor, Proxer proxer) {
    this.constructor = constructor;
    this.proxer = proxer;
  }

  public static Proxer wrapping(Class<? extends RuntimeException> type, Proxer proxer) {
    check(type != null);
    check(proxer != null);
    try {
      return new WrappingProxer(type.getConstructor(Throwable.class), proxer);
    } catch (NoSuchMethodException e) {
      throw new ProxyException(e);
    }
  }

  public Object proxy(Typing typing, Handler handler) {
    check(typing != null);
    check(handler != null);
    try {
      return proxer.proxy(typing, wrapping(handler));
    } catch (ProxyException e) {
      throw wrap(e);
    }
  }

  private Handler wrapping(final Handler handler) {
    return new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        try {
          return handler.handle(invocation);
        } catch (ProxyException e) {
          throw wrap(e);
        }
      }
    };
  }

  private RuntimeException wrap(ProxyException exception) {
    try {
      return constructor.newInstance(exception);
    } catch (ReflectiveOperationException e) {
      throw new ProxyException(e);
    }
  }
}
