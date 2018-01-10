package org.testory.proxy.proxer;

import static org.testory.proxy.ProxyException.check;
import static org.testory.proxy.Typing.typing;

import org.testory.proxy.Handler;
import org.testory.proxy.Proxer;
import org.testory.proxy.Typing;

public class FixObjectBugProxer implements Proxer {
  private final Proxer proxer;

  private FixObjectBugProxer(Proxer proxer) {
    this.proxer = proxer;
  }

  public static Proxer fixObjectBug(Proxer proxer) {
    check(proxer != null);
    return new FixObjectBugProxer(proxer);
  }

  public Object proxy(Typing typing, Handler handler) {
    check(typing != null);
    check(handler != null);
    return proxer.proxy(fix(typing), handler);
  }

  private static Typing fix(Typing typing) {
    return typing.superclass == Object.class
        ? typing(ProxiableObject.class, typing.interfaces)
        : typing;
  }

  public static class ProxiableObject {}
}
