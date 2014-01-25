package org.testory.proxy;

import static org.testory.common.Checks.checkArgument;
import static org.testory.common.Checks.checkNotNull;
import static org.testory.proxy.Invocation.invocation;
import static org.testory.proxy.Typing.typing;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.sf.cglib.core.CodeGenerationException;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;

import org.objenesis.ObjenesisStd;

public class Proxies {
  public static boolean isProxiable(Class<?> type) {
    checkNotNull(type);
    return !Modifier.isFinal(type.getModifiers());
  }

  /**
   * Creates new proxy instance that handles invocations with specified <b>handler</b> and extends
   * superclass and implements interfaces of <b>typing</b>.
   * <p>
   * <ul>
   * <li>
   * If extending type has final method, it is not intercepted and original method from extended
   * type is invoked instead. This may cause unexpected effects.</li>
   * <li>Invoking method of intercepted invocation from within <b>handler</b> causes infinite
   * recursion and throws {@link StackOverflowError}.</li>
   * <li>If <b>handler</b> returns null and handled method return type is primitive, then null will
   * be replaced by binary zero.</li>
   * <li>{@link Object#finalize()} is never intercepted</li>
   * </ul>
   */
  public static Object proxy(Typing typing, Handler handler) {
    checkNotNull(typing);
    checkNotNull(handler);
    return newProxyByCglib(tryAsProxiable(typing), handler);
  }

  private static Object newProxyByCglib(Typing typing, final Handler handler) {
    Enhancer enhancer = new Enhancer() {
      /** includes all constructors */
      protected void filterConstructors(Class sc, List constructors) {}
    };
    enhancer.setClassLoader(classLoadersFor(typing));
    enhancer.setUseFactory(true);
    enhancer.setSuperclass(typing.superclass);
    enhancer.setInterfaces(typing.interfaces.toArray(new Class[0]));
    enhancer.setCallbackTypes(new Class[] { MethodInterceptor.class, NoOp.class });
    enhancer.setCallbackFilter(new CallbackFilter() {
      /** ignores bridge methods */
      public int accept(Method method) {
        return method.isBridge()
            ? 1
            : 0;
      }
    });
    Class<?> proxyClass;
    try {
      proxyClass = enhancer.createClass();
    } catch (CodeGenerationException e) {
      throw new IllegalArgumentException(e);
    }

    Factory proxy = (Factory) new ObjenesisStd().newInstance(proxyClass);
    proxy.setCallbacks(new Callback[] { asMethodInterceptor(handler), new SerializableNoOp() });
    return proxy;
  }

  private static Typing tryAsProxiable(Typing typing) {
    return tryWithoutFactory(tryWithoutObjectBecauseOfCglibBug(typing));
  }

  private static Typing tryWithoutFactory(Typing typing) {
    return Arrays.asList(typing.superclass.getInterfaces()).contains(Factory.class)
        ? withoutFactory(typing)
        : typing;
  }

  private static Typing withoutFactory(Typing typing) {
    Class<?> superclass = typing.superclass.getSuperclass();
    Set<Class<?>> interfaces = new HashSet<Class<?>>();
    interfaces.addAll(Arrays.asList(typing.superclass.getInterfaces()));
    interfaces.addAll(typing.interfaces);
    interfaces.remove(Factory.class);
    return typing(superclass, interfaces);
  }

  private static Typing tryWithoutObjectBecauseOfCglibBug(Typing typing) {
    class ProxiableObject {}
    return typing.superclass == Object.class
        ? typing(ProxiableObject.class, typing.interfaces)
        : typing;
  }

  private static ClassLoader classLoadersFor(Typing typing) {
    LinkedHashSet<ClassLoader> loaders = new LinkedHashSet<ClassLoader>();
    loaders.add(typing.superclass.getClassLoader());
    loaders.add(Proxies.class.getClassLoader());
    loaders.add(Thread.currentThread().getContextClassLoader());
    loaders.remove(null);
    checkArgument(!loaders.isEmpty());
    return chain(loaders);
  }

  private static ClassLoader chain(Iterable<ClassLoader> loaders) {
    ClassLoader loader;
    Iterator<ClassLoader> iterator = loaders.iterator();
    loader = iterator.next();
    while (iterator.hasNext()) {
      loader = chain(loader, iterator.next());
    }
    return loader;
  }

  private static ClassLoader chain(ClassLoader first, final ClassLoader second) {
    return new ClassLoader(first) {
      protected Class<?> findClass(String name) throws ClassNotFoundException {
        return second.loadClass(name);
      }
    };
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
