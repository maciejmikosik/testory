package org.testory.proxy.proxer;

import static org.testory.proxy.ProxyException.check;

import java.lang.reflect.Modifier;

import org.testory.proxy.Handler;
import org.testory.proxy.Proxer;
import org.testory.proxy.Typing;

public class NonFinalProxer implements Proxer {
  private final Proxer proxer;

  private NonFinalProxer(Proxer proxer) {
    this.proxer = proxer;
  }

  public static Proxer nonFinal(Proxer proxer) {
    check(proxer != null);
    return new NonFinalProxer(proxer);
  }

  public Object proxy(Typing typing, Handler handler) {
    check(!Modifier.isFinal(typing.superclass.getModifiers()));
    return proxer.proxy(typing, handler);
  }
}
