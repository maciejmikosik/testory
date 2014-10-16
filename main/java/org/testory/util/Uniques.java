package org.testory.util;

import static org.testory.common.Checks.checkArgument;
import static org.testory.common.Checks.checkNotNull;
import static org.testory.proxy.Proxies.isProxiable;
import static org.testory.proxy.Proxies.proxy;
import static org.testory.proxy.Typing.typing;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;

import org.objenesis.ObjenesisStd;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.Typing;

public class Uniques {
  public static boolean hasUniques(Class<?> type) {
    checkNotNull(type);
    return !type.isPrimitive();
  }

  public static <T> T unique(Class<T> type) {
    checkNotNull(type);
    checkArgument(hasUniques(type));
    return isProxiable(type)
        ? newProxyInstance(type)
        : type.isArray()
            ? (T) Array.newInstance(type.getComponentType(), 0)
            : new ObjenesisStd().newInstance(type);
  }

  private static <T> T newProxyInstance(Class<T> type) {
    Typing typing = type.isInterface()
        ? typing(Object.class, new HashSet<Class<?>>(Arrays.asList(type)))
        : typing(type, new HashSet<Class<?>>());
    Handler handler = new Handler() {
      public Object handle(Invocation invocation) {
        throw new UnsupportedOperationException();
      }
    };
    return (T) proxy(typing, handler);
  }
}
