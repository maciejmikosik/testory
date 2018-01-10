package org.testory.proxy.handler;

import static java.lang.String.format;

import org.testory.common.Nullable;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;

public class ReturningHandler implements Handler {
  private final Object object;

  private ReturningHandler(Object object) {
    this.object = object;
  }

  public static Handler returning(@Nullable Object object) {
    return new ReturningHandler(object);
  }

  public Object handle(Invocation invocation) {
    return object;
  }

  public String toString() {
    return format("returning(%s)", object);
  }
}
