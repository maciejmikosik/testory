package org.testory.util;

import static org.testory.common.Classes.setAccessible;
import static org.testory.proxy.ProxyException.check;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.testory.proxy.Invocation;

public class Invocations {
  public static Object invoke(Invocation invocation) throws Throwable {
    check(invocation != null);
    final Method method = invocation.method;
    Object instance = invocation.instance;
    Object[] arguments = invocation.arguments.toArray();
    setAccessible(method);
    try {
      return method.invoke(instance, arguments);
    } catch (InvocationTargetException e) {
      throw e.getCause();
    } catch (ReflectiveOperationException e) {
      throw new Error(e);
    }
  }
}
