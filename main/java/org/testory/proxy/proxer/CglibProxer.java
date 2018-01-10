package org.testory.proxy.proxer;

import static org.testory.proxy.Invocation.invocation;
import static org.testory.proxy.ProxyException.check;
import static org.testory.proxy.Typing.typing;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objenesis.ObjenesisStd;
import org.testory.proxy.Handler;
import org.testory.proxy.Proxer;
import org.testory.proxy.Typing;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;

public class CglibProxer implements Proxer {
  public Object proxy(Typing typing, Handler handler) {
    check(typing != null);
    check(handler != null);
    return newProxyByCglib(tryWithoutFactory(typing), handler);
  }

  private static Object newProxyByCglib(Typing typing, Handler handler) {
    Enhancer enhancer = new Enhancer() {
      /** includes all constructors */
      protected void filterConstructors(Class sc, List constructors) {}
    };
    enhancer.setClassLoader(Thread.currentThread().getContextClassLoader());
    enhancer.setUseFactory(true);
    enhancer.setSuperclass(typing.superclass);
    enhancer.setInterfaces(typing.interfaces.toArray(new Class[0]));
    enhancer.setCallbackTypes(new Class[] { MethodInterceptor.class, NoOp.class });
    enhancer.setCallbackFilter(new CallbackFilter() {
      /** ignores bridge methods */
      public int accept(Method method) {
        return method.isBridge() ? 1 : 0;
      }
    });
    Class<?> proxyClass = enhancer.createClass();
    Factory proxy = (Factory) new ObjenesisStd().newInstance(proxyClass);
    proxy.setCallbacks(new Callback[] { asMethodInterceptor(handler), new SerializableNoOp() });
    return proxy;
  }

  private static Typing tryWithoutFactory(Typing typing) {
    return Arrays.asList(typing.superclass.getInterfaces()).contains(Factory.class)
        ? withoutFactory(typing)
        : typing;
  }

  private static Typing withoutFactory(Typing typing) {
    Typing peeled = typing.peel();
    Class<?> superclass = peeled.superclass;
    Set<Class<?>> interfaces = new HashSet<>(peeled.interfaces);
    interfaces.remove(Factory.class);
    return typing(superclass, interfaces);
  }

  private static MethodInterceptor asMethodInterceptor(final Handler handler) {
    return new MethodInterceptor() {
      public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
          throws Throwable {
        return isFinalize(method)
            ? null
            : handler.handle(invocation(method, obj, Arrays.asList(args)));
      }
    };
  }

  private static boolean isFinalize(Method method) {
    return method.getName().equals("finalize") && method.getParameterTypes().length == 0;
  }

  private static class SerializableNoOp implements NoOp, Serializable {
    private static final long serialVersionUID = 4961170565306875478L;

    private SerializableNoOp() {}
  }
}
