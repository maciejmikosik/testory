package org.testory.proxy;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.hash;
import static org.testory.common.Collections.immutable;
import static org.testory.proxy.ProxyException.check;

import java.util.HashSet;
import java.util.Set;

/**
 * Runtime (after erasure) type signature of concrete/abstract class. <br>
 * For example:<br>
 * public final class Integer <b>extends Number implements Comparable</b>
 */
public class Typing {
  public final Class<?> superclass;
  public final Set<Class<?>> interfaces;

  private Typing(Class<?> superclass, Set<Class<?>> interfaces) {
    this.superclass = superclass;
    this.interfaces = interfaces;
  }

  public static Typing typing(Class<?> superclass, Set<? extends Class<?>> interfaces) {
    check(superclass != null);
    check(interfaces != null);
    check(!superclass.isInterface());
    check(!superclass.isPrimitive());
    check(!superclass.isArray());
    Set<Class<?>> interfacesCopy = immutable(interfaces);
    for (Class<?> interfacee : interfacesCopy) {
      check(interfacee.isInterface() && !interfacee.isAnnotation());
    }
    return new Typing(superclass, interfacesCopy);
  }

  public static Typing extending(Class<?> type) {
    return typing(type, new HashSet<Class<?>>());
  }

  public static Typing implementing(Class<?>... type) {
    return typing(Object.class, new HashSet<Class<?>>(asList(type)));
  }

  public static Typing subclassing(Class<?>... types) {
    check(types != null);
    Set<Class<?>> superclasses = new HashSet<>();
    Set<Class<?>> interfaces = new HashSet<>();
    for (Class<?> type : types) {
      check(type != null);
      if (type.isInterface()) {
        interfaces.add(type);
      } else {
        superclasses.add(type);
      }
    }
    if (superclasses.size() > 1) {
      throw new ProxyException();
    } else if (superclasses.size() == 0) {
      superclasses.add(Object.class);
    }
    return typing(superclasses.iterator().next(), interfaces);
  }

  public Typing peel() {
    check(superclass != Object.class);
    Class<?> peeledSuperclass = superclass.getSuperclass();
    Set<Class<?>> peeledInterfaces = new HashSet<>(interfaces);
    peeledInterfaces.addAll(asList(superclass.getInterfaces()));
    return typing(peeledSuperclass, peeledInterfaces);

  }

  public boolean equals(Object object) {
    return this == object || object instanceof Typing && equalsTyping((Typing) object);
  }

  private boolean equalsTyping(Typing typing) {
    return superclass == typing.superclass && interfaces.equals(typing.interfaces);
  }

  public int hashCode() {
    return hash(superclass, interfaces);
  }

  public String toString() {
    return format("typing(%s, %s)", superclass, interfaces);
  }
}
