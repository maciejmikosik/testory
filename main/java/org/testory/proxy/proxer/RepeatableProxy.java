package org.testory.proxy.proxer;

import static org.testory.proxy.ProxyException.check;
import static org.testory.proxy.Typing.typing;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.testory.proxy.Handler;
import org.testory.proxy.Proxer;
import org.testory.proxy.Typing;

import net.sf.cglib.proxy.Factory;

public class RepeatableProxy implements Proxer {
  private final Proxer proxer;

  private RepeatableProxy(Proxer proxer) {
    this.proxer = proxer;
  }

  public static Proxer repeatable(Proxer proxer) {
    check(proxer != null);
    return new RepeatableProxy(proxer);
  }

  public Object proxy(Typing typing, Handler handler) {
    check(typing != null);
    check(handler != null);
    return proxer.proxy(tryWithoutFactory(typing), handler);
  }

  private static Typing tryWithoutFactory(Typing typing) {
    return Arrays.asList(typing.superclass.getInterfaces()).contains(Factory.class)
        ? withoutFactory(typing)
        : typing;
  }

  private static Typing withoutFactory(Typing typing) {
    Typing peeled = typing.peel();
    Class<?> superclass = peeled.superclass;
    Set<Class<?>> interfaces = new HashSet<>(peeled.interfaces);
    interfaces.remove(Factory.class);
    return typing(superclass, interfaces);
  }
}
