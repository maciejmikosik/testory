package org.testory.proxy.$;

import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static java.util.Collections.unmodifiableSortedMap;
import static java.util.Collections.unmodifiableSortedSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.common.Classes.defaultValue;
import static org.testory.proxy.Invocation.invocation;
import static org.testory.testing.Fakes.newObject;
import static org.testory.testing.Fakes.newThrowable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NavigableSet;
import java.util.RandomAccess;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;
import org.testory.proxy.CglibProxer;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.Proxer;
import org.testory.proxy.ProxyException;
import org.testory.proxy.Typing;

import net.sf.cglib.core.CodeGenerationException;

public class test_CglibProxer {
  private Proxer proxer;
  private Handler handler;
  private Typing typing;
  private Invocation savedInvocation;
  private Object object;
  private Foo proxy;
  private Method method;
  private Throwable throwable;
  private int counter;
  public boolean invoked;

  @Before
  public void before() throws NoSuchMethodException {
    proxer = new CglibProxer();
    typing = typing(Foo.class);
    method = Foo.class.getDeclaredMethod("getObject");
    handler = handlerReturning(null);
    object = newObject("object");
    throwable = newThrowable("throwable");
  }

  abstract class Foo {
    public void setObject(Object foo) {
      throw new RuntimeException();
    }

    public Object getObject() {
      throw new RuntimeException();
    }

    public String getString() {
      throw new RuntimeException();
    }

    public boolean getBoolean() {
      throw new RuntimeException();
    }

    public int getInt() {
      throw new RuntimeException();
    }

    public float getFloat() {
      throw new RuntimeException();
    }

    public void getVoid() {
      throw new RuntimeException();
    }

    public void throwsIOException() throws IOException {
      throw new RuntimeException();
    }

    protected Object clone() {
      throw new RuntimeException();
    }

    protected void finalize() {
      throw new RuntimeException();
    }

    void packagePrivate() {}

    protected abstract void protectedAbstract();
  }

  @Test
  public void can_proxy_concrete_classes() {
    class NestedConcreteClass {}

    new TestProxer(proxer)
        .canProxy(Object.class)
        .canProxy($ConcreteClass.class)
        .canProxy($PackagePrivateConcreteClass.class)
        .canProxy(NestedConcreteClass.class)
        .canProxy($ConcreteClassWithPrivateDefaultConstructor.class)
        .canProxy($ConcreteClassWithPrivateConstructorWithArguments.class);
  }

  @Test
  public void can_proxy_abstract_classes() {
    new TestProxer(proxer)
        .canProxy($AbstractClassWithAbstractMethod.class)
        .canProxy($AbstractClassWithProtectedAbstractMethod.class);
  }

  @Test
  public void can_proxy_public_interfaces() {
    new TestProxer(proxer)
        .canProxy(typing($InterfaceA.class, $InterfaceB.class, $InterfaceC.class));
  }

  @Test
  public void cannot_proxy_package_private_interfaces() {
    try {
      proxer.proxy(typing($PackagePrivateInterface.class), handler);
      fail();
    } catch (CodeGenerationException e) {}
  }

  @Test
  public void can_proxy_duplicated_interfaces() {
    class Superclass implements $InterfaceA {}
    new TestProxer(proxer)
        .canProxy(typing(Superclass.class, $InterfaceA.class));
  }

  @Test
  public void can_proxy_other_proxy_types() {
    new TestProxer(proxer)
        .canProxy(
            typing(
                proxer.proxy(typing($ConcreteClass.class, $InterfaceA.class), handler).getClass(),
                $InterfaceB.class),
            typing(
                $ConcreteClass.class,
                $InterfaceA.class,
                $InterfaceB.class))
        .canProxy(
            typing(
                proxer.proxy(typing(), handler).getClass(),
                $InterfaceA.class),
            typing(
                $InterfaceA.class))
        .canProxy(
            typing(
                proxer.proxy(typing($ConcreteClass.class, $InterfaceA.class), handler).getClass(),
                $InterfaceA.class),
            typing(
                $ConcreteClass.class,
                $InterfaceA.class));
  }

  @Test
  public void can_proxy_jdk_collections() {
    ArrayList<Object> arrayList = new ArrayList<Object>();
    LinkedList<Object> linkedList = new LinkedList<Object>();
    HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
    TreeMap<Object, Object> treeMap = new TreeMap<Object, Object>();
    HashSet<Object> hashSet = new HashSet<Object>();
    TreeSet<Object> treeSet = new TreeSet<Object>();

    new TestProxer(proxer)
        .canProxy(arrayList)
        .canProxy(arrayList.iterator(), typing(Iterator.class))
        .canProxy(arrayList.listIterator(), typing(ListIterator.class))
        .canProxy(arrayList.subList(0, 0), typing(AbstractList.class, RandomAccess.class))
        .canProxy(arrayList.subList(0, 0).iterator(), typing(Iterator.class))
        .canProxy(arrayList.subList(0, 0).listIterator(), typing(ListIterator.class))
        .canProxy(linkedList)
        .canProxy(linkedList.iterator(), typing(Iterator.class))
        .canProxy(linkedList.listIterator(), typing(ListIterator.class))
        .canProxy(linkedList.subList(0, 0), typing(AbstractList.class))
        .canProxy(linkedList.subList(0, 0).iterator(), typing(Iterator.class))
        .canProxy(linkedList.subList(0, 0).listIterator(), typing(ListIterator.class))
        .canProxy(hashMap)
        .canProxy(hashMap.keySet(), typing(AbstractSet.class))
        .canProxy(hashMap.keySet().iterator(), typing(Iterator.class))
        .canProxy(hashMap.values(), typing(AbstractCollection.class))
        .canProxy(hashMap.values().iterator(), typing(Iterator.class))
        .canProxy(hashMap.entrySet(), typing(AbstractSet.class))
        .canProxy(hashMap.entrySet().iterator(), typing(Iterator.class))
        .canProxy(treeMap)
        .canProxy(treeMap.keySet(), typing(AbstractSet.class, NavigableSet.class))
        .canProxy(treeMap.keySet().iterator(), typing(Iterator.class))
        .canProxy(treeMap.values(), typing(AbstractCollection.class))
        .canProxy(treeMap.values().iterator(), typing(Iterator.class))
        .canProxy(treeMap.entrySet(), typing(AbstractSet.class))
        .canProxy(treeMap.entrySet().iterator(), typing(Iterator.class))
        .canProxy(hashSet)
        .canProxy(hashSet.iterator(), typing(Iterator.class))
        .canProxy(treeSet)
        .canProxy(treeSet.iterator(), typing(Iterator.class));
  }

  @Test
  public void can_proxy_arrays_as_list() {
    List<Object> list = Arrays.asList();

    new TestProxer(proxer)
        .canProxy(list, typing(AbstractList.class, RandomAccess.class, Serializable.class))
        .canProxy(list.iterator(), typing(Iterator.class))
        .canProxy(list.listIterator(), typing(ListIterator.class))
        .canProxy(list.subList(0, 0), typing(AbstractList.class, RandomAccess.class))
        .canProxy(list.subList(0, 0).iterator(), typing(Iterator.class))
        .canProxy(list.subList(0, 0).listIterator(), typing(ListIterator.class));
  }

  @Test
  public void can_proxy_unmodifiable_collections() {
    Collection<Object> collection = unmodifiableCollection(new ArrayList<Object>());
    List<Object> list = unmodifiableList(new LinkedList<Object>());
    List<Object> randomAccessList = unmodifiableList(new ArrayList<Object>());
    Set<Object> set = unmodifiableSet(new HashSet<Object>());
    SortedSet<Object> sortedSet = unmodifiableSortedSet(new TreeSet<Object>());
    Map<Object, Object> map = unmodifiableMap(new HashMap<Object, Object>());
    SortedMap<Object, Object> sortedMap = unmodifiableSortedMap(new TreeMap<Object, Object>());

    new TestProxer(proxer)
        .canProxy(collection, typing(Collection.class, Serializable.class))
        .canProxy(collection.iterator(), typing(Iterator.class))
        .canProxy(list, typing(List.class, Serializable.class))
        .canProxy(list.iterator(), typing(Iterator.class))
        .canProxy(list.listIterator(), typing(ListIterator.class))
        .canProxy(list.subList(0, 0), typing(List.class))
        .canProxy(list.subList(0, 0).iterator(), typing(Iterator.class))
        .canProxy(list.subList(0, 0).listIterator(), typing(ListIterator.class))
        .canProxy(randomAccessList, typing(List.class, Serializable.class, RandomAccess.class))
        .canProxy(randomAccessList.iterator(), typing(Iterator.class))
        .canProxy(randomAccessList.listIterator(), typing(ListIterator.class))
        .canProxy(randomAccessList.subList(0, 0), typing(List.class, RandomAccess.class))
        .canProxy(randomAccessList.subList(0, 0).iterator(), typing(Iterator.class))
        .canProxy(randomAccessList.subList(0, 0).listIterator(), typing(ListIterator.class))
        .canProxy(set, typing(Set.class, Serializable.class))
        .canProxy(set.iterator(), typing(Iterator.class))
        .canProxy(sortedSet, typing(SortedSet.class, Serializable.class))
        .canProxy(sortedSet.iterator(), typing(Iterator.class))
        .canProxy(map, typing(Map.class, Serializable.class))
        .canProxy(map.keySet(), typing(Set.class, Serializable.class))
        .canProxy(map.keySet().iterator(), typing(Iterator.class))
        .canProxy(map.values(), typing(Collection.class, Serializable.class))
        .canProxy(map.values().iterator(), typing(Iterator.class))
        .canProxy(map.entrySet(), typing(Set.class, Serializable.class))
        .canProxy(map.entrySet().iterator(), typing(Iterator.class))
        .canProxy(sortedMap, typing(SortedMap.class, Serializable.class))
        .canProxy(sortedMap.keySet(), typing(Set.class, Serializable.class))
        .canProxy(sortedMap.keySet().iterator(), typing(Iterator.class))
        .canProxy(sortedMap.values(), typing(Collection.class, Serializable.class))
        .canProxy(sortedMap.values().iterator(), typing(Iterator.class))
        .canProxy(sortedMap.entrySet(), typing(Set.class, Serializable.class))
        .canProxy(sortedMap.entrySet().iterator(), typing(Iterator.class));
  }

  @Test
  public void cannot_proxy_final_classes() {
    final class FinalClass {}
    try {
      proxer.proxy(typing(FinalClass.class), handler);
      fail();
    } catch (IllegalArgumentException e) {}
  }

  @Test
  public void intercepts_invocation() throws NoSuchMethodException {
    proxy = (Foo) proxer.proxy(typing, handlerSavingInvocation());
    method = Foo.class.getDeclaredMethod("setObject", Object.class);
    proxy.setObject(object);
    assertEquals(invocation(method, proxy, Arrays.asList(object)), savedInvocation);
  }

  @Test
  public void intercepts_equals() throws NoSuchMethodException {
    proxy = (Foo) proxer.proxy(typing, handlerSavingInvocation());
    method = Object.class.getDeclaredMethod("equals", Object.class);
    proxy.equals(object);
    assertEquals(invocation(method, proxy, Arrays.asList(object)), savedInvocation);
  }

  @Test
  public void intercepts_to_string() throws NoSuchMethodException {
    proxy = (Foo) proxer.proxy(typing, handlerSavingInvocation());
    method = Object.class.getDeclaredMethod("toString");
    proxy.toString();
    assertEquals(invocation(method, proxy, Arrays.asList()), savedInvocation);
  }

  @Test
  public void intercepts_clone() throws NoSuchMethodException {
    proxy = (Foo) proxer.proxy(typing, handlerSavingInvocation());
    method = Foo.class.getDeclaredMethod("clone");
    proxy.clone();
    assertEquals(invocation(method, proxy, Arrays.asList()), savedInvocation);
  }

  @Test
  public void does_not_intercept_finalize() {
    handler = new Handler() {
      public Object handle(Invocation invocation) {
        return counter++;
      }
    };
    proxy = (Foo) proxer.proxy(typing, handler);
    proxy.finalize();
    assertEquals(0, counter);
  }

  @Test
  public void intercepts_package_private_method() throws NoSuchMethodException {
    proxy = (Foo) proxer.proxy(typing, handlerSavingInvocation());
    method = Foo.class.getDeclaredMethod("packagePrivate");
    proxy.packagePrivate();
    assertEquals(invocation(method, proxy, Arrays.asList()), savedInvocation);
  }

  @Test
  public void intercepts_protected_abstract_method() throws NoSuchMethodException {
    proxy = (Foo) proxer.proxy(typing, handlerSavingInvocation());
    method = Foo.class.getDeclaredMethod("protectedAbstract");
    proxy.protectedAbstract();
    assertEquals(invocation(method, proxy, Arrays.asList()), savedInvocation);
  }

  @Test
  public void returns_object() {
    proxy = (Foo) proxer.proxy(typing, handlerReturning(object));
    assertSame(object, proxy.getObject());
  }

  @Test
  public void returned_object_is_ignored_for_void_method() {
    proxy = (Foo) proxer.proxy(typing, handlerReturning(object));
    proxy.getVoid();
  }

  @Test
  public void returns_primitive() {
    proxy = (Foo) proxer.proxy(typing, handlerReturning(3));
    assertEquals(3, proxy.getInt());
  }

  @Test
  public void returned_primitive_is_wide_converted() {
    proxy = (Foo) proxer.proxy(typing, handlerReturning(3));
    assertEquals(3f, proxy.getFloat(), 0f);
  }

  @Test
  public void returned_primitive_is_narrow_converted() {
    proxy = (Foo) proxer.proxy(typing, handlerReturning(3f));
    assertEquals(3, proxy.getInt());
  }

  @Test
  public void returns_null() {
    proxy = (Foo) proxer.proxy(typing, handlerReturning(null));
    assertEquals(null, proxy.getObject());
  }

  @Test
  public void returned_null_is_converted_to_void() {
    proxy = (Foo) proxer.proxy(typing, handlerReturning(null));
    proxy.getVoid();
  }

  @Test
  public void returned_null_is_converted_to_primitive_zero() {
    proxy = (Foo) proxer.proxy(typing, handlerReturning(null));
    assertEquals(0, proxy.getInt());
  }

  @Test
  public void does_not_return_incompatible_type() {
    proxy = (Foo) proxer.proxy(typing, handlerReturning(object));
    try {
      proxy.getString();
      fail();
    } catch (ClassCastException e) {}
  }

  @Test
  public void throws_error() {
    throwable = new Error();
    proxy = (Foo) proxer.proxy(typing, handlerThrowing(throwable));
    try {
      proxy.getObject();
      fail();
    } catch (Throwable t) {
      assertSame(throwable, t);
    }
  }

  @Test
  public void throws_runtime_exception() {
    throwable = new RuntimeException();
    proxy = (Foo) proxer.proxy(typing, handlerThrowing(throwable));
    try {
      proxy.getObject();
      fail();
    } catch (Throwable t) {
      assertSame(throwable, t);
    }
  }

  @Test
  public void throws_declared_exception() {
    throwable = new IOException();
    proxy = (Foo) proxer.proxy(typing, handlerThrowing(throwable));
    try {
      proxy.throwsIOException();
      fail();
    } catch (Throwable t) {
      assertSame(throwable, t);
    }
  }

  @Test
  public void throws_subclass_of_declared_exception() {
    throwable = new FileNotFoundException();
    proxy = (Foo) proxer.proxy(typing, handlerThrowing(throwable));
    try {
      proxy.throwsIOException();
      fail();
    } catch (Throwable t) {
      assertSame(throwable, t);
    }
  }

  @Test
  public void throws_undeclared_exception() {
    throwable = new IOException();
    proxy = (Foo) proxer.proxy(typing, handlerThrowing(throwable));
    try {
      proxy.getObject();
      fail();
    } catch (Throwable t) {
      assertSame(throwable, t);
    }
  }

  @Test
  public void throws_superclass_of_declared_exception() throws IOException {
    throwable = new Exception();
    proxy = (Foo) proxer.proxy(typing, handlerThrowing(throwable));
    try {
      proxy.throwsIOException();
      fail();
    } catch (Throwable t) {
      assertSame(throwable, t);
    }
  }

  @Test
  public void does_not_invoke_constructor() {
    @SuppressWarnings("unused")
    class Proxiable {
      public Proxiable() {
        invoked = true;
      }
    }
    proxer.proxy(typing(Proxiable.class), handler);
    assertFalse(invoked);
  }

  /* must be public for cross-classloader inheritance test */
  public abstract class PublicFoo extends Foo {}

  @Test
  public void uses_thread_context_class_loader() {
    Thread thread = Thread.currentThread();
    ClassLoader original = thread.getContextClassLoader();
    ClassLoader context = new ClassLoader(original) {};
    assertSame(original, PublicFoo.class.getClassLoader());
    try {
      thread.setContextClassLoader(context);
      proxy = (Foo) proxer.proxy(typing(PublicFoo.class), handler);
      assertSame(context, proxy.getClass().getClassLoader());
    } finally {
      thread.setContextClassLoader(original);
    }
  }

  @Test
  public void recursion_causes_stack_overflow() {
    proxy = (Foo) proxer.proxy(typing, new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        try {
          return invocation.method.invoke(invocation.instance, invocation.arguments.toArray());
        } catch (InvocationTargetException e) {
          throw e.getCause();
        }
      }
    });
    try {
      proxy.toString();
      fail();
    } catch (StackOverflowError e) {}
  }

  @Test
  public void typing_cannot_be_null() {
    try {
      proxer.proxy(null, handler);
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void handler_cannot_be_null() {
    try {
      proxer.proxy(typing, null);
      fail();
    } catch (ProxyException e) {}
  }

  private static Handler handlerReturning(final Object object) {
    return new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        return object;
      }
    };
  }

  private static Handler handlerThrowing(final Throwable throwable) {
    return new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        throw throwable;
      }
    };
  }

  private Handler handlerSavingInvocation() {
    return new Handler() {
      public Object handle(Invocation invocation) {
        savedInvocation = invocation;
        return defaultValue(invocation.method.getReturnType());
      }
    };
  }

  private static Typing typing(Class<?>... types) {
    Set<Class<?>> superclasses = new HashSet<Class<?>>();
    Set<Class<?>> interfaces = new HashSet<Class<?>>();
    for (Class<?> type : types) {
      Set<Class<?>> typeCollection = type.isInterface()
          ? interfaces
          : superclasses;
      typeCollection.add(type);
    }
    if (superclasses.size() > 1) {
      throw new IllegalArgumentException();
    } else if (superclasses.size() == 0) {
      superclasses.add(Object.class);
    }
    return Typing.typing(superclasses.iterator().next(), interfaces);
  }

  private static class TestProxer {
    private final Proxer proxer;

    public TestProxer(Proxer proxer) {
      this.proxer = proxer;
    }

    public TestProxer canProxy(Object object) {
      Typing typing = typing(object.getClass());
      return canProxy(typing, typing);
    }

    public TestProxer canProxy(Class<?> type) {
      Typing typing = typing(type);
      return canProxy(typing, typing);
    }

    public TestProxer canProxy(Typing typing) {
      return canProxy(typing, typing);
    }

    public TestProxer canProxy(Object object, Typing outgoing) {
      return canProxy(typing(object.getClass()), outgoing);
    }

    public TestProxer canProxy(Typing incoming, Typing outgoing) {
      String message = incoming + " " + outgoing;
      Object proxy = proxer.proxy(incoming, handlerReturning(null));
      assertTrue(message, outgoing.superclass.isInstance(proxy));
      for (Class<?> type : outgoing.interfaces) {
        assertTrue(message, type.isInstance(proxy));
      }
      return this;
    }
  }
}

class $PackagePrivateConcreteClass {}

interface $PackagePrivateInterface {}