package org.testory.mock;

import static org.testory.common.Checks.checkArgument;

import java.util.Collections;
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
    checkArgument(!superclass.isInterface());
    checkArgument(!superclass.isPrimitive());
    checkArgument(!superclass.isArray());
    Set<Class<?>> interfacesCopy = Collections.unmodifiableSet(new HashSet<Class<?>>(interfaces));
    for (Class<?> interfacee : interfacesCopy) {
      checkArgument(interfacee.isInterface() && !interfacee.isAnnotation());
    }
    return new Typing(superclass, interfacesCopy);
  }

  public boolean equals(Object object) {
    return this == object || object instanceof Typing && equalsTyping((Typing) object);
  }

  private boolean equalsTyping(Typing typing) {
    return superclass == typing.superclass && interfaces.equals(typing.interfaces);
  }

  public int hashCode() {
    return ((0xFFFF + superclass.hashCode()) * 0xFFFF + interfaces.hashCode()) * 0xFFFF;
  }

  public String toString() {
    return "typing(" + superclass + ", " + interfaces + ")";
  }
}
