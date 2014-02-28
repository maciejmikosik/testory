package org.testory.proxy;

import static java.util.Collections.unmodifiableList;
import static org.testory.common.Checks.checkArgument;
import static org.testory.common.Checks.checkNotNull;
import static org.testory.common.Classes.canAssign;
import static org.testory.common.Objects.areEqualDeep;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
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
    checkNotNull(instance);
    checkArgument(!Modifier.isStatic(method.getModifiers()));
    checkArgument(method.getDeclaringClass().isInstance(instance));

    List<Object> args = unmodifiableList(new ArrayList<Object>(arguments));
    Class<?>[] parameters = method.getParameterTypes();
    checkArgument(parameters.length == args.size());
    for (int i = 0; i < parameters.length; i++) {
      checkArgument(canAssign(args.get(i), parameters[i]));
    }
    return new Invocation(method, instance, args);
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
      if (!areEqualDeep(arguments.get(i), invocation.arguments.get(i))) {
        return false;
      }
    }
    return true;
  }

  public int hashCode() {
    return (method.hashCode() * 0xFFFF + instance.hashCode()) * 0xFFFF + arguments.hashCode();
  }

  public String toString() {
    return "invocation(" + method + ", " + instance + ", " + arguments + ")";
  }
}
