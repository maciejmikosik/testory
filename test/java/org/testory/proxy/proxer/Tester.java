package org.testory.proxy.proxer;

import static java.lang.String.format;
import static org.junit.Assert.assertTrue;
import static org.testory.proxy.Typing.subclassing;
import static org.testory.proxy.handler.ReturningHandler.returning;

import org.testory.proxy.Proxer;
import org.testory.proxy.Typing;

public class Tester {
  private final Proxer proxer;

  private Tester(Proxer proxer) {
    this.proxer = proxer;
  }

  public static Tester tester(Proxer proxer) {
    return new Tester(proxer);
  }

  public Tester canProxy(Object object) {
    Typing typing = subclassing(object.getClass());
    return canProxy(typing, typing);
  }

  public Tester canProxy(Class<?> type) {
    Typing typing = subclassing(type);
    return canProxy(typing, typing);
  }

  public Tester canProxy(Typing typing) {
    return canProxy(typing, typing);
  }

  public Tester canProxy(Object object, Typing outgoing) {
    return canProxy(subclassing(object.getClass()), outgoing);
  }

  public Tester canProxy(Typing incoming, Typing outgoing) {
    String message = format("%s %s", incoming, outgoing);
    Object proxy = proxer.proxy(incoming, returning(null));
    assertTrue(message, outgoing.superclass.isInstance(proxy));
    for (Class<?> type : outgoing.interfaces) {
      assertTrue(message, type.isInstance(proxy));
    }
    return this;
  }
}
