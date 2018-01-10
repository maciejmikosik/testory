package org.testory.proxy.handler;

import static java.lang.String.format;
import static org.testory.proxy.ProxyException.check;

import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;

public class ThrowingHandler implements Handler {
  private final Throwable throwable;

  private ThrowingHandler(Throwable throwable) {
    this.throwable = throwable;
  }

  public static Handler throwing(Throwable throwable) {
    check(throwable != null);
    return new ThrowingHandler(throwable);
  }

  public Object handle(Invocation invocation) throws Throwable {
    throw throwable;
  }

  public String toString() {
    return format("throwing(%s)", throwable);
  }
}
