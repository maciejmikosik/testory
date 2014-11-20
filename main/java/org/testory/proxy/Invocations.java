package org.testory.proxy;

import static org.testory.common.Classes.setAccessible;
import static org.testory.proxy.ProxyException.check;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Invocations {
  public static Object invoke(Invocation invocation) throws Throwable {
    check(invocation != null);
    Method method = invocation.method;
    Object instance = invocation.instance;
    Object[] arguments = invocation.arguments.toArray();
    setAccessible(method);
    try {
      return method.invoke(instance, arguments);
    } catch (InvocationTargetException e) {
      throw e.getCause();
    } catch (ReflectiveOperationException e) {
      throw new ProxyException(e);
    }
  }
}
