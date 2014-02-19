package org.testory.proxy;

import static java.util.Collections.unmodifiableList;
import static org.testory.common.Checks.checkArgument;
import static org.testory.common.Checks.checkNotNull;
import static org.testory.common.Objects.areEqualDeep;

import java.lang.reflect.InvocationTargetException;
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
      checkArgument(isAssignableTo(parameters[i], args.get(i)));
    }
    return new Invocation(method, instance, args);
  }

  // TODO extract common isAssignableTo(Class, Object)
  public static boolean isAssignableTo(Class<?> type, Object instance) {
    // FIXME fails it type==void.class
    return type.isPrimitive()
        ? isConvertibleTo(type, instance)
        : instance == null || type.isAssignableFrom(instance.getClass());
  }

  private static boolean isConvertibleTo(Class<?> type, Object instance) {
    try {
      Method method = PrimitiveMethods.class.getDeclaredMethod("method", type);
      method.setAccessible(true);
      method.invoke(null, instance);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    } catch (NoSuchMethodException e) {
      throw new Error(e);
    } catch (IllegalAccessException e) {
      throw new Error(e);
    } catch (InvocationTargetException e) {
      throw new Error(e);
    }
  }

  @SuppressWarnings("unused")
  private static class PrimitiveMethods {
    private static void method(byte argument) {}

    private static void method(short argument) {}

    private static void method(int argument) {}

    private static void method(long argument) {}

    private static void method(float argument) {}

    private static void method(double argument) {}

    private static void method(boolean argument) {}

    private static void method(char argument) {}
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
