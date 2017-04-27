package org.testory.proxy.handler;

import static org.testory.common.Classes.defaultValue;
import static org.testory.proxy.ProxyException.check;

import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;

public class ReturningDefaultValueHandler implements Handler {
  private final Handler handler;

  private ReturningDefaultValueHandler(Handler handler) {
    this.handler = handler;
  }

  public static Handler returningDefaultValue(Handler handler) {
    check(handler != null);
    return new ReturningDefaultValueHandler(handler);
  }

  public Object handle(Invocation invocation) throws Throwable {
    handler.handle(invocation);
    return defaultValue(invocation.method.getReturnType());
  }
}
