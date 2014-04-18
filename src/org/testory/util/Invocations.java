package org.testory.util;

import static org.testory.proxy.ProxyException.check;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.testory.proxy.Invocation;

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
}
