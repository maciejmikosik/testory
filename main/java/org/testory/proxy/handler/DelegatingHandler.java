package org.testory.proxy.handler;

import static org.testory.proxy.Invocation.invocation;
import static org.testory.proxy.ProxyException.check;

import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;

public class DelegatingHandler implements Handler {
  private final Object instance;
  private final Handler handler;

  private DelegatingHandler(Object instance, Handler handler) {
    this.instance = instance;
    this.handler = handler;
  }

  public static Handler delegatingTo(Object instance, Handler handler) {
    check(instance != null);
    check(handler != null);
    return new DelegatingHandler(instance, handler);
  }

  public Object handle(Invocation invocation) throws Throwable {
    return handler.handle(invocation(invocation.method, instance, invocation.arguments));
  }
}
