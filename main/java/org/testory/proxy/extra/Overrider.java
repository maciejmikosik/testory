package org.testory.proxy.extra;

import static org.testory.proxy.ProxyException.check;
import static org.testory.proxy.Typing.extending;
import static org.testory.proxy.handler.DelegatingHandler.delegatingTo;

import org.testory.proxy.Handler;
import org.testory.proxy.Proxer;

public class Overrider {
  private final Proxer proxer;

  private Overrider(Proxer proxer) {
    this.proxer = proxer;
  }

  public static Overrider overrider(Proxer proxer) {
    check(proxer != null);
    return new Overrider(proxer);
  }

  public <T> T override(T instance, Handler handler) {
    check(instance != null);
    check(handler != null);
    return (T) proxer.proxy(
        extending(instance.getClass()),
        delegatingTo(instance, handler));
  }
}
