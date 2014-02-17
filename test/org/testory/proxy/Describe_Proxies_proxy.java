package org.testory.proxy;

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
import static org.testory.proxy.Invocation.invocation;
import static org.testory.proxy.Proxies.isProxiable;
import static org.testory.proxy.Proxies.proxy;
import static org.testory.test.Testilities.newObject;
import static org.testory.test.Testilities.newThrowable;

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

public class Describe_Proxies_proxy {
  private Handler handler;
  private Typing typing;
  private Invocation savedInvocation;
  private Object object, proxy;
  private Method method;
  private Throwable throwable;
  private int counter;
  private Class<?> type;

  @Before
  public void before() throws NoSuchMethodException {
    type = Object.class;
    typing = typing();
    method = Object.class.getDeclaredMethod("toString");
    handler = new Handler() {
      public Object handle(Invocation invocation) {
        return null;
      }
    };
    object = newObject("object");
    throwable = newThrowable("throwable");
  }

  @Test
  public void concrete_classes_are_proxiable() {
    assertProxiable(Object.class);
    assertProxiable($ConcreteClass.class);
    assertProxiable($PackagePrivateConcreteClass.class);
    class NestedConcreteClass {}
    assertProxiable(NestedConcreteClass.class);
    assertProxiable($ConcreteClassWithPrivateDefaultConstructor.class);
    assertProxiable($ConcreteClassWithPrivateConstructorWithArguments.class);
  }

  @Test
  public void abstract_classes_are_proxiable() {
    assertProxiable($AbstractClassWithAbstractMethod.class);
    assertProxiable($AbstractClassWithProtectedAbstractMethod.class);
  }

  @Test
  public void interfaces_are_proxiable() {
    assertProxiable(typing($InterfaceA.class, $InterfaceB.class, $InterfaceC.class));
    assertProxiable(typing($PackagePrivateInterfaceA.class, $PackagePrivateInterfaceB.class,
        $PackagePrivateInterfaceC.class));
  }

  @Test
  public void merges_duplicated_interfaces() {
    class Superclass implements $InterfaceA {}
    assertProxiable(typing(Superclass.class, $InterfaceA.class));
  }

  @Test
  public void proxy_types_are_proxiable() {
    type = proxy(typing($ConcreteClass.class, $InterfaceA.class), handler).getClass();
    assertProxiable(typing(type, $InterfaceB.class),
        typing($ConcreteClass.class, $InterfaceA.class, $InterfaceB.class));

    type = proxy(typing(), handler).getClass();
    assertProxiable(typing(type, $InterfaceA.class), typing($InterfaceA.class));

    type = proxy(typing($ConcreteClass.class, $InterfaceA.class), handler).getClass();
    assertProxiable(typing(type, $InterfaceA.class),
        typing($ConcreteClass.class, $InterfaceA.class));
  }

  @Test
  public void collections_are_proxiable() {
    ArrayList<Object> arrayList = new ArrayList<Object>();
    assertProxiable(arrayList);
    assertProxiable(arrayList.iterator(), typing(Iterator.class));
    assertProxiable(arrayList.listIterator(), typing(ListIterator.class));
    assertProxiable(arrayList.subList(0, 0), typing(AbstractList.class, RandomAccess.class));
    assertProxiable(arrayList.subList(0, 0).iterator(), typing(Iterator.class));
    assertProxiable(arrayList.subList(0, 0).listIterator(), typing(ListIterator.class));

    LinkedList<Object> linkedList = new LinkedList<Object>();
    assertProxiable(linkedList);
    assertProxiable(linkedList.iterator(), typing(Iterator.class));
    assertProxiable(linkedList.listIterator(), typing(ListIterator.class));
    assertProxiable(linkedList.subList(0, 0), typing(AbstractList.class));
    assertProxiable(linkedList.subList(0, 0).iterator(), typing(Iterator.class));
    assertProxiable(linkedList.subList(0, 0).listIterator(), typing(ListIterator.class));

    HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
    assertProxiable(hashMap);
    assertProxiable(hashMap.keySet(), typing(AbstractSet.class));
    assertProxiable(hashMap.keySet().iterator(), typing(Iterator.class));
    assertProxiable(hashMap.values(), typing(AbstractCollection.class));
    assertProxiable(hashMap.values().iterator(), typing(Iterator.class));
    assertProxiable(hashMap.entrySet(), typing(AbstractSet.class));
    assertProxiable(hashMap.entrySet().iterator(), typing(Iterator.class));

    TreeMap<Object, Object> treeMap = new TreeMap<Object, Object>();
    assertProxiable(treeMap);
    assertProxiable(treeMap.keySet(), typing(AbstractSet.class, NavigableSet.class));
    assertProxiable(treeMap.keySet().iterator(), typing(Iterator.class));
    assertProxiable(treeMap.values(), typing(AbstractCollection.class));
    assertProxiable(treeMap.values().iterator(), typing(Iterator.class));
    assertProxiable(treeMap.entrySet(), typing(AbstractSet.class));
    assertProxiable(treeMap.entrySet().iterator(), typing(Iterator.class));

    HashSet<Object> hashSet = new HashSet<Object>();
    assertProxiable(hashSet);
    assertProxiable(hashSet.iterator(), typing(Iterator.class));

    TreeSet<Object> treeSet = new TreeSet<Object>();
    assertProxiable(treeSet);
    assertProxiable(treeSet.iterator(), typing(Iterator.class));
  }

  @Test
  public void arrays_as_list_is_proxiable() {
    List<Object> list = Arrays.asList();
    assertProxiable(list, typing(AbstractList.class, RandomAccess.class, Serializable.class));
    assertProxiable(list.iterator(), typing(Iterator.class));
    assertProxiable(list.listIterator(), typing(ListIterator.class));
    assertProxiable(list.subList(0, 0), typing(AbstractList.class, RandomAccess.class));
    assertProxiable(list.subList(0, 0).iterator(), typing(Iterator.class));
    assertProxiable(list.subList(0, 0).listIterator(), typing(ListIterator.class));
  }

  @Test
  public void unmodifiable_collections_are_proxiable() {
    Collection<Object> collection = unmodifiableCollection(new ArrayList<Object>());
    assertProxiable(collection, typing(Collection.class, Serializable.class));
    assertProxiable(collection.iterator(), typing(Iterator.class));

    List<Object> list = unmodifiableList(new LinkedList<Object>());
    assertProxiable(list, typing(List.class, Serializable.class));
    assertProxiable(list.iterator(), typing(Iterator.class));
    assertProxiable(list.listIterator(), typing(ListIterator.class));
    assertProxiable(list.subList(0, 0), typing(List.class));
    assertProxiable(list.subList(0, 0).iterator(), typing(Iterator.class));
    assertProxiable(list.subList(0, 0).listIterator(), typing(ListIterator.class));

    List<Object> randomAccessList = unmodifiableList(new ArrayList<Object>());
    assertProxiable(randomAccessList, typing(List.class, Serializable.class, RandomAccess.class));
    assertProxiable(randomAccessList.iterator(), typing(Iterator.class));
    assertProxiable(randomAccessList.listIterator(), typing(ListIterator.class));
    assertProxiable(randomAccessList.subList(0, 0), typing(List.class, RandomAccess.class));
    assertProxiable(randomAccessList.subList(0, 0).iterator(), typing(Iterator.class));
    assertProxiable(randomAccessList.subList(0, 0).listIterator(), typing(ListIterator.class));

    Set<Object> set = unmodifiableSet(new HashSet<Object>());
    assertProxiable(set, typing(Set.class, Serializable.class));
    assertProxiable(set.iterator(), typing(Iterator.class));

    SortedSet<Object> sortedSet = unmodifiableSortedSet(new TreeSet<Object>());
    assertProxiable(sortedSet, typing(SortedSet.class, Serializable.class));
    assertProxiable(sortedSet.iterator(), typing(Iterator.class));

    Map<Object, Object> map = unmodifiableMap(new HashMap<Object, Object>());
    assertProxiable(map, typing(Map.class, Serializable.class));
    assertProxiable(map.keySet(), typing(Set.class, Serializable.class));
    assertProxiable(map.keySet().iterator(), typing(Iterator.class));
    assertProxiable(map.values(), typing(Collection.class, Serializable.class));
    assertProxiable(map.values().iterator(), typing(Iterator.class));
    assertProxiable(map.entrySet(), typing(Set.class, Serializable.class));
    assertProxiable(map.entrySet().iterator(), typing(Iterator.class));

    SortedMap<Object, Object> sortedMap = unmodifiableSortedMap(new TreeMap<Object, Object>());
    assertProxiable(sortedMap, typing(SortedMap.class, Serializable.class));
    assertProxiable(sortedMap.keySet(), typing(Set.class, Serializable.class));
    assertProxiable(sortedMap.keySet().iterator(), typing(Iterator.class));
    assertProxiable(sortedMap.values(), typing(Collection.class, Serializable.class));
    assertProxiable(sortedMap.values().iterator(), typing(Iterator.class));
    assertProxiable(sortedMap.entrySet(), typing(Set.class, Serializable.class));
    assertProxiable(sortedMap.entrySet().iterator(), typing(Iterator.class));
  }

  private static void assertProxiable(Object object) {
    Typing typing = typing(object.getClass());
    assertProxiable(typing, typing);
  }

  private static void assertProxiable(Class<?> type) {
    Typing typing = typing(type);
    assertProxiable(typing, typing);
  }

  private static void assertProxiable(Typing typing) {
    assertProxiable(typing, typing);
  }

  private static void assertProxiable(Object object, Typing outgoing) {
    assertProxiable(typing(object.getClass()), outgoing);
  }

  private static void assertProxiable(Typing incoming, Typing outgoing) {
    String message = incoming + " " + outgoing;
    assertTrue(message, isProxiable(incoming.superclass));
    for (Class<?> type : incoming.interfaces) {
      assertTrue(message, isProxiable(type));
    }
    Object proxy = proxy(incoming, new Handler() {
      public Object handle(Invocation invocation) {
        return null;
      }
    });
    assertTrue(message, outgoing.superclass.isInstance(proxy));
    for (Class<?> type : outgoing.interfaces) {
      assertTrue(message, type.isInstance(proxy));
    }
  }

  @Test
  public void should_not_create_proxy_extending_final_type() {
    type = $FinalClass.class;
    assertFalse(isProxiable(type));
    try {
      proxy(typing(type), handler);
      fail();
    } catch (IllegalArgumentException e) {}
  }

  @Test
  public final void should_intercept_invocation() throws NoSuchMethodException {
    class Foo {
      public Object foo(Object foo) {
        return null;
      }
    }
    typing = typing(Foo.class);
    proxy = proxy(typing, handlerSavingInvocation());
    method = Foo.class.getDeclaredMethod("foo", Object.class);
    ((Foo) proxy).foo(object);
    assertEquals(invocation(method, proxy, Arrays.asList(object)), savedInvocation);
  }

  @Test
  public void should_intercept_equals() throws NoSuchMethodException {
    proxy = proxy(typing, handlerSavingInvocation());
    method = Object.class.getDeclaredMethod("equals", Object.class);
    proxy.equals(object);
    assertEquals(invocation(method, proxy, Arrays.asList(object)), savedInvocation);
  }

  @Test
  public void should_intercept_to_string() throws NoSuchMethodException {
    proxy = proxy(typing, handlerSavingInvocation());
    method = Object.class.getDeclaredMethod("toString");
    proxy.toString();
    assertEquals(invocation(method, proxy, Arrays.asList()), savedInvocation);
  }

  @Test
  public void should_intercept_clone() throws NoSuchMethodException {
    typing = typing($ConcreteClassWithClone.class);
    proxy = proxy(typing, handlerSavingInvocation());
    method = $ConcreteClassWithClone.class.getDeclaredMethod("clone");
    (($ConcreteClassWithClone) proxy).clone();
    assertEquals(invocation(method, proxy, Arrays.asList()), savedInvocation);
  }

  @Test
  public void should_not_intercept_finalize() {
    handler = new Handler() {
      public Object handle(Invocation invocation) {
        return counter++;
      }
    };
    typing = typing($ConcreteClassWithFinalize.class);
    proxy = proxy(typing, handler);
    (($ConcreteClassWithFinalize) proxy).finalize();
    assertEquals(0, counter);
  }

  @Test
  public void should_intercept_package_private_method() throws NoSuchMethodException {
    typing = typing($ConcreteClassWithPackagePrivateMethod.class);
    proxy = proxy(typing, handlerSavingInvocation());
    method = $ConcreteClassWithPackagePrivateMethod.class.getDeclaredMethod("packagePrivateMethod");
    (($ConcreteClassWithPackagePrivateMethod) proxy).packagePrivateMethod();
    assertEquals(invocation(method, proxy, Arrays.asList()), savedInvocation);
  }

  @Test
  public void should_intercept_protected_abstract_method() throws NoSuchMethodException {
    typing = typing($AbstractClassWithProtectedAbstractMethod.class);
    proxy = proxy(typing, handlerSavingInvocation());
    method = $AbstractClassWithProtectedAbstractMethod.class.getDeclaredMethod("abstractMethod");
    (($AbstractClassWithProtectedAbstractMethod) proxy).abstractMethod();
    assertEquals(invocation(method, proxy, Arrays.asList()), savedInvocation);
  }

  @Test
  public void should_return_result_from_handler() {
    class Foo {
      public Object foo() {
        return null;
      }
    }
    proxy = proxy(typing(Foo.class), new Handler() {
      public Object handle(Invocation interceptedInvocation) {
        return object;
      }
    });
    assertSame(object, ((Foo) proxy).foo());
  }

  @Test
  public void should_not_return_incompatible_type_from_handler() {
    class Foo {
      public String foo() {
        return null;
      }
    }
    proxy = proxy(typing(Foo.class), new Handler() {
      public Object handle(Invocation interceptedInvocation) {
        return object;
      }
    });
    try {
      ((Foo) proxy).foo();
      fail();
    } catch (ClassCastException e) {}
  }

  @Test
  public void should_throw_throwable_from_handler() {
    class Foo {
      public Object foo() throws Throwable {
        return null;
      }
    }
    proxy = proxy(typing(Foo.class), new Handler() {
      public Object handle(Invocation interceptedInvocation) throws Throwable {
        throw throwable;
      }
    });
    try {
      ((Foo) proxy).foo();
      fail();
    } catch (Throwable t) {
      assertSame(throwable, t);
    }
  }

  @Test
  public void should_throw_incompatible_throwable_from_handler() {
    class Foo {
      public Object foo() {
        return null;
      }
    }
    proxy = proxy(typing(Foo.class), new Handler() {
      public Object handle(Invocation interceptedInvocation) throws Throwable {
        throw throwable;
      }
    });
    try {
      ((Foo) proxy).foo();
      fail();
    } catch (Throwable t) {
      assertSame(throwable, t);
    }
  }

  @Test
  public void should_stack_overflow_for_handler_invoking_invocation() {
    proxy = proxy(typing, new Handler() {
      public Object handle(Invocation interceptedInvocation) throws Throwable {
        try {
          return interceptedInvocation.method.invoke(interceptedInvocation.instance,
              interceptedInvocation.arguments.toArray());
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
  public void should_null_returned_by_handler_be_converted_to_zero() {
    class Foo {
      public int foo() {
        return 0;
      }
    }
    proxy = proxy(typing(Foo.class), new Handler() {
      public Object handle(Invocation interceptedInvocation) {
        return null;
      }
    });
    assertEquals(0, ((Foo) proxy).foo());
  }

  @Test
  public final void should_fail_for_null_typing() {
    try {
      proxy(null, handler);
      fail();
    } catch (NullPointerException e) {}
  }

  @Test
  public final void should_fail_for_null_handler() {
    try {
      proxy(typing, null);
      fail();
    } catch (NullPointerException e) {}
  }

  private Handler handlerSavingInvocation() {
    return new Handler() {
      public Object handle(Invocation invocation) {
        savedInvocation = invocation;
        return null;
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
}

class $PackagePrivateConcreteClass {}

interface $PackagePrivateInterfaceA {}

interface $PackagePrivateInterfaceB {}

interface $PackagePrivateInterfaceC {}
