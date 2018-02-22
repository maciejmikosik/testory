package org.testory.proxy;

import static java.lang.String.format;
import static java.util.Objects.deepEquals;
import static org.testory.common.Classes.canInvoke;
import static org.testory.common.Classes.setAccessible;
import static org.testory.common.Collections.immutable;
import static org.testory.proxy.ProxyException.check;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

public class Invocation {
  public final Method method;
  public final Object instance;
  public final List<Object> arguments;

  private Invocation(Method method, Object instance, List<Object> arguments) {
    this.method = method;
    this.instance = instance;
    this.arguments = arguments;
  }

  public static Invocation invocation(Method method, Object instance, List<?> arguments) {
    check(method != null);
    check(!Modifier.isStatic(method.getModifiers()));
    check(arguments != null);
    check(canInvoke(method, instance, arguments.toArray()));
    return new Invocation(method, instance, immutable(arguments));
  }

  public Object invoke() throws Throwable {
    setAccessible(method);
    try {
      return method.invoke(instance, arguments.toArray());
    } catch (InvocationTargetException e) {
      throw e.getCause();
    } catch (ReflectiveOperationException e) {
      throw new Error(e);
    }
  }

  public boolean equals(Object object) {
    return object instanceof Invocation && equals((Invocation) object);
  }

  private boolean equals(Invocation invocation) {
    return method.equals(invocation.method) && instance == invocation.instance
        && equalsArgumentsOf(invocation);
  }

  private boolean equalsArgumentsOf(Invocation invocation) {
    for (int i = 0; i < arguments.size(); i++) {
      if (!deepEquals(arguments.get(i), invocation.arguments.get(i))) {
        return false;
      }
    }
    return true;
  }

  public int hashCode() {
    return (method.hashCode() * 0xFFFF + instance.hashCode()) * 0xFFFF + arguments.hashCode();
  }

  public String toString() {
    return format("invocation(%s, %s, %s)", method, instance, arguments);
  }
}
