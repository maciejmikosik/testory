package org.testory.proxy;

import static org.testory.common.Checks.checkNotNull;
import static org.testory.proxy.ProxyException.check;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.testory.common.Matcher;
import org.testory.common.Nullable;

public class Invocations {
  public static Object invoke(Invocation invocation) throws Throwable {
    check(invocation != null);
    final Method method = invocation.method;
    Object instance = invocation.instance;
    Object[] arguments = invocation.arguments.toArray();
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
      public Void run() {
        method.setAccessible(true);
        return null;
      }
    });
    try {
      return method.invoke(instance, arguments);
    } catch (IllegalAccessException e) {
      throw new Error(e);
    } catch (InvocationTargetException e) {
      throw e.getCause();
    }
  }

  public static Matcher invocationOf(final Matcher method, final Matcher instance,
      final Matcher arguments) {
    checkNotNull(method);
    checkNotNull(instance);
    checkNotNull(arguments);
    return new Matcher() {
      public boolean matches(@Nullable Object item) {
        Invocation invocation = (Invocation) item;
        return method.matches(invocation.method) && instance.matches(invocation.instance)
            && arguments.matches(invocation.arguments);
      }

      public String toString() {
        return "invocationOf(" + method + ", " + instance + ", " + arguments + ")";
      }
    };
  }
}
