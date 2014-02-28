package org.testory.proxy;

import static org.testory.common.Checks.checkArgument;
import static org.testory.common.Classes.canReturn;
import static org.testory.common.Classes.canThrow;
import static org.testory.proxy.Invocation.invocation;
import static org.testory.proxy.ProxyException.check;
import static org.testory.proxy.Typing.typing;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

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
    check(type != null);
    return !isFinal(type) || isPeelable(type);
  }

  private static boolean isFinal(Class<?> type) {
    return Modifier.isFinal(type.getModifiers());
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
    check(typing != null);
    check(handler != null);
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
      throw new ProxyException(e);
    } catch (IllegalArgumentException e) {
      throw new ProxyException(e);
    }

    Factory proxy = (Factory) new ObjenesisStd().newInstance(proxyClass);
    proxy.setCallbacks(new Callback[] { asMethodInterceptor(compatible(handler)),
        new SerializableNoOp() });
    return proxy;
  }

  private static Typing tryAsProxiable(Typing typing) {
    return tryPeel(tryWithoutFactory(tryWithoutObjectBecauseOfCglibBug(typing)));
  }

  private static Typing tryPeel(Typing typing) {
    return isPeelable(typing.superclass)
        ? tryPeel(peel(typing))
        : typing;
  }

  private static boolean isPeelable(Class<?> type) {
    return peelables.contains(type);
  }

  private static final Set<Class<?>> peelables = peelables();

  private static Set<Class<?>> peelables() {
    HashSet<Class<?>> classes = new HashSet<Class<?>>();

    ArrayList<Object> arrayList = new ArrayList<Object>();
    classes.add(arrayList.iterator().getClass());
    classes.add(arrayList.listIterator().getClass());
    classes.add(arrayList.subList(0, 0).getClass());
    classes.add(arrayList.subList(0, 0).iterator().getClass());

    LinkedList<Object> linkedList = new LinkedList<Object>();
    classes.add(linkedList.iterator().getClass());
    classes.add(linkedList.subList(0, 0).getClass());
    classes.add(linkedList.subList(0, 0).iterator().getClass());

    HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
    classes.add(hashMap.keySet().getClass());
    classes.add(hashMap.keySet().iterator().getClass());
    classes.add(hashMap.values().getClass());
    classes.add(hashMap.values().iterator().getClass());
    classes.add(hashMap.entrySet().getClass());
    classes.add(hashMap.entrySet().iterator().getClass());
    classes.add(hashMap.keySet().iterator().getClass().getSuperclass());

    TreeMap<Object, Object> treeMap = new TreeMap<Object, Object>();
    classes.add(treeMap.keySet().getClass());
    classes.add(treeMap.keySet().iterator().getClass());
    classes.add(treeMap.values().getClass());
    classes.add(treeMap.values().iterator().getClass());
    classes.add(treeMap.entrySet().getClass());
    classes.add(treeMap.entrySet().iterator().getClass());
    classes.add(treeMap.keySet().iterator().getClass().getSuperclass());

    classes.add(Arrays.asList().getClass());
    classes.add(Arrays.asList().iterator().getClass());
    classes.add(Arrays.asList().listIterator().getClass());
    classes.add(Arrays.asList().subList(0, 0).getClass());

    HashSet<Object> hashSet = new HashSet<Object>();
    TreeSet<Object> treeSet = new TreeSet<Object>();

    Collection<Object> uCollection = Collections.unmodifiableCollection(arrayList);
    List<Object> uList = Collections.unmodifiableList(linkedList);
    List<Object> uRandomAccessList = Collections.unmodifiableList(arrayList);
    Set<Object> uSet = Collections.unmodifiableSet(hashSet);
    SortedSet<Object> uSortedSet = Collections.unmodifiableSortedSet(treeSet);
    Map<Object, Object> uMap = Collections.unmodifiableMap(hashMap);
    SortedMap<Object, Object> uSortedMap = Collections.unmodifiableSortedMap(treeMap);
    classes.add(uCollection.getClass());
    classes.add(uCollection.iterator().getClass());
    classes.add(uList.getClass());
    classes.add(uList.listIterator().getClass());
    classes.add(uRandomAccessList.getClass());
    classes.add(uSet.getClass());
    classes.add(uSortedSet.getClass());
    classes.add(uMap.getClass());
    classes.add(uMap.entrySet().getClass());
    classes.add(uMap.entrySet().iterator().getClass());
    classes.add(uSortedMap.getClass());

    return Collections.unmodifiableSet(classes);
  }

  private static Typing tryWithoutFactory(Typing typing) {
    return Arrays.asList(typing.superclass.getInterfaces()).contains(Factory.class)
        ? withoutFactory(typing)
        : typing;
  }

  private static Typing withoutFactory(Typing typing) {
    Typing peeled = peel(typing);
    Class<?> superclass = peeled.superclass;
    Set<Class<?>> interfaces = new HashSet<Class<?>>(peeled.interfaces);
    interfaces.remove(Factory.class);
    return typing(superclass, interfaces);
  }

  private static Typing peel(Typing typing) {
    Class<?> superclass = typing.superclass.getSuperclass();
    Set<Class<?>> interfaces = new HashSet<Class<?>>(typing.interfaces);
    interfaces.addAll(Arrays.asList(typing.superclass.getInterfaces()));
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

  private static Handler compatible(final Handler handler) {
    return new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        Object returned;
        try {
          returned = handler.handle(invocation);
        } catch (Throwable throwable) {
          check(canThrow(throwable, invocation.method));
          throw throwable;
        }
        check(canReturn(returned, invocation.method) || canReturnVoid(returned, invocation.method));
        return returned;
      }

      private boolean canReturnVoid(Object returned, Method method) {
        return method.getReturnType() == void.class && returned == null;
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
