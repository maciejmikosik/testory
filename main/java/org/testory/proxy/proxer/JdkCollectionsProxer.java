package org.testory.proxy.proxer;

import static java.lang.reflect.Modifier.isPublic;
import static org.testory.proxy.ProxyException.check;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.testory.proxy.Handler;
import org.testory.proxy.Proxer;
import org.testory.proxy.Typing;

public class JdkCollectionsProxer implements Proxer {
  private final Proxer proxer;

  private JdkCollectionsProxer(Proxer proxer) {
    this.proxer = proxer;
  }

  public static Proxer jdkCollections(Proxer proxer) {
    check(proxer != null);
    return new JdkCollectionsProxer(proxer);
  }

  public Object proxy(Typing typing, Handler handler) {
    check(typing != null);
    check(handler != null);
    return proxer.proxy(peelUntilNeeded(typing), handler);
  }

  private static Typing peelUntilNeeded(Typing typing) {
    return requiresPeeling(typing.superclass)
        ? peelUntilNeeded(typing.peel())
        : typing;
  }

  private static boolean requiresPeeling(Class<?> type) {
    return isFromJdk(type)
        && isContainer(type)
        && !isPublic(type.getModifiers());
  }

  private static boolean isFromJdk(Class<?> type) {
    return type.getPackage() == Package.getPackage("java.util");
  }

  private static boolean isContainer(Class<?> type) {
    return Collection.class.isAssignableFrom(type)
        || Map.class.isAssignableFrom(type)
        || Iterator.class.isAssignableFrom(type)
        || (type != null && isContainer(type.getDeclaringClass()));
  }
}
