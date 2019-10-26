package org.testory.proxy.proxer;

import static org.testory.proxy.Invocation.invocation;
import static org.testory.proxy.ProxyException.check;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import org.objenesis.ObjenesisStd;
import org.testory.proxy.Handler;
import org.testory.proxy.Proxer;
import org.testory.proxy.Typing;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.matcher.ElementMatchers;

public class ByteBuddyProxer implements Proxer {
  private ByteBuddyProxer() {}

  public static Proxer byteBuddyProxer() {
    return new ByteBuddyProxer();
  }

  public Object proxy(Typing typing, Handler handler) {
    check(typing != null);
    check(handler != null);

    Class<?> decorableType = new ByteBuddy()
        .subclass(typing.superclass)
        .implement(new ArrayList<>(typing.interfaces))
        .method(ElementMatchers.any())
        .intercept(MethodDelegation.to(asByteBuddy(handler)))
        .make()
        .load(Thread.currentThread().getContextClassLoader(), ClassLoadingStrategy.Default.INJECTION)
        .getLoaded();
    return new ObjenesisStd().newInstance(decorableType);
  }

  private static DecorateHandler asByteBuddy(Handler handler) {
    return new DecorateHandler(handler);
  }

  public static class DecorateHandler {
    private final Handler handler;

    public DecorateHandler(Handler handler) {
      this.handler = handler;
    }

    @RuntimeType
    public Object handle(
        @This Object instance,
        @Origin Method method,
        @AllArguments Object[] arguments) throws Throwable {
      return isFinalize(method)
          ? null
          : handler.handle(invocation(method, instance, Arrays.asList(arguments)));
    }
  }

  private static boolean isFinalize(Method method) {
    return method.getName().equals("finalize")
        && method.getParameterTypes().length == 0;
  }
}
