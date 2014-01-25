package org.testory.proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.proxy.Invocation.invocation;
import static org.testory.proxy.Proxies.isProxiable;
import static org.testory.proxy.Proxies.proxy;
import static org.testory.proxy.Typing.typing;
import static org.testory.test.Testilities.newObject;
import static org.testory.test.Testilities.newThrowable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
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
  private Set<Class<?>> interfaces;

  @Before
  public void before() throws NoSuchMethodException {
    type = Object.class;
    interfaces = new HashSet<Class<?>>();
    typing = typing(type, interfaces);
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
  public void should_create_proxy_extending_object() {
    type = Object.class;
    assertTrue(isProxiable(type));
    proxy = proxy(typing(type, interfaces), handler);
    assertTrue(type.isInstance(proxy));
  }

  @Test
  public void should_create_proxy_extending_concrete_class() {
    type = $ConcreteClass.class;
    assertTrue(isProxiable(type));
    proxy = proxy(typing(type, interfaces), handler);
    assertTrue(type.isInstance(proxy));
  }

  @Test
  public void should_create_proxy_extending_package_private_concrete_class() {
    type = $PackagePrivateConcreteClass.class;
    assertTrue(isProxiable(type));
    proxy = proxy(typing(type, interfaces), handler);
    assertTrue(type.isInstance(proxy));
  }

  @Test
  public void should_create_proxy_extending_nested_in_method_concrete_class() {
    class NestedConcreteClass {}
    type = NestedConcreteClass.class;
    assertTrue(isProxiable(type));
    proxy = proxy(typing(type, interfaces), handler);
    assertTrue(type.isInstance(proxy));
  }

  @Test
  public void should_create_proxy_extending_abstract_class_with_abstract_method() {
    type = $AbstractClassWithAbstractMethod.class;
    assertTrue(isProxiable(type));
    proxy = proxy(typing(type, interfaces), handler);
    assertTrue(type.isInstance(proxy));
  }

  @Test
  public void should_create_proxy_extending_abstract_class_with_protected_abstract_method() {
    type = $AbstractClassWithProtectedAbstractMethod.class;
    assertTrue(isProxiable(type));
    proxy = proxy(typing(type, interfaces), handler);
    assertTrue(type.isInstance(proxy));
  }

  @Test
  public void should_create_proxy_implementing_many_interfaces() {
    interfaces = interfaces($InterfaceA.class, $InterfaceB.class, $InterfaceC.class);
    for (Class<?> interfaceType : interfaces) {
      assertTrue(isProxiable(interfaceType));
    }
    proxy = proxy(typing(type, interfaces), handler);
    for (Class<?> interfaceType : interfaces) {
      assertTrue(interfaceType.isInstance(proxy));
    }
  }

  // TODO fix: proxy of package private interfaces
  @Ignore
  @Test
  public void should_create_proxy_implementing_many_package_private_interfaces() {
    interfaces = interfaces($PackagePrivateInterfaceA.class, $PackagePrivateInterfaceB.class,
        $PackagePrivateInterfaceC.class);
    for (Class<?> interfaceType : interfaces) {
      assertTrue(isProxiable(interfaceType));
    }
    proxy = proxy(typing(type, interfaces), handler);
    for (Class<?> interfaceType : interfaces) {
      assertTrue(interfaceType.isInstance(proxy));
    }
  }

  @Test
  public void should_create_proxy_extending_type_of_other_proxy() {
    type = proxy(typing($ConcreteClass.class, interfaces($InterfaceA.class)), handler).getClass();
    assertTrue(isProxiable(type));
    proxy = proxy(typing(type, interfaces($InterfaceB.class)), handler);
    assertTrue(proxy instanceof $ConcreteClass);
    assertTrue(proxy instanceof $InterfaceA);
    assertTrue(proxy instanceof $InterfaceB);
  }

  @Test
  public void should_create_proxy_extending_type_of_other_proxy_extending_object() {
    type = proxy(typing(Object.class, interfaces), handler).getClass();
    assertTrue(isProxiable(type));
    proxy = proxy(typing(type, interfaces($InterfaceA.class)), handler);
    assertTrue(proxy instanceof $InterfaceA);
  }

  @Test
  public void should_create_proxy_extending_concrete_class_with_private_default_constructor() {
    type = $ConcreteClassWithPrivateDefaultConstructor.class;
    assertTrue(isProxiable(type));
    proxy = proxy(typing(type, interfaces), handler);
    assertTrue(type.isInstance(proxy));
  }

  @Test
  public void should_create_proxy_extending_concrete_class_with_private_constructor_with_arguments() {
    type = $ConcreteClassWithPrivateConstructorWithArguments.class;
    assertTrue(isProxiable(type));
    proxy = proxy(typing(type, interfaces), handler);
    assertTrue(type.isInstance(proxy));
  }

  @Test
  public void should_create_proxy_extending_type_of_other_proxy_and_implementing_duplicated_interface() {
    type = proxy(typing($ConcreteClass.class, interfaces($InterfaceA.class)), handler).getClass();
    assertTrue(isProxiable(type));
    proxy = proxy(typing(type, interfaces($InterfaceA.class)), handler);
    assertTrue(proxy instanceof $ConcreteClass);
    assertTrue(proxy instanceof $InterfaceA);
  }

  @Test
  public void should_create_proxy_with_duplicated_interfaces() {
    class Superclass implements $InterfaceA {}
    type = Superclass.class;
    interfaces = interfaces($InterfaceA.class);
    proxy = proxy(typing(type, interfaces), handler);
    assertTrue(proxy instanceof Superclass);
    assertTrue(proxy instanceof $InterfaceA);
  }

  @Test
  public void should_not_create_proxy_extending_final_type() {
    type = $FinalClass.class;
    assertFalse(isProxiable(type));
    try {
      proxy(typing(type, interfaces), handler);
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
    typing = typing(Foo.class, interfaces);
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
    typing = typing($ConcreteClassWithClone.class, interfaces);
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
    typing = typing($ConcreteClassWithFinalize.class, interfaces);
    proxy = proxy(typing, handler);
    (($ConcreteClassWithFinalize) proxy).finalize();
    assertEquals(0, counter);
  }

  @Test
  public void should_intercept_package_private_method() throws NoSuchMethodException {
    typing = typing($ConcreteClassWithPackagePrivateMethod.class, interfaces);
    proxy = proxy(typing, handlerSavingInvocation());
    method = $ConcreteClassWithPackagePrivateMethod.class.getDeclaredMethod("packagePrivateMethod");
    (($ConcreteClassWithPackagePrivateMethod) proxy).packagePrivateMethod();
    assertEquals(invocation(method, proxy, Arrays.asList()), savedInvocation);
  }

  @Test
  public void should_intercept_protected_abstract_method() throws NoSuchMethodException {
    typing = typing($AbstractClassWithProtectedAbstractMethod.class, interfaces);
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
    proxy = proxy(typing(Foo.class, interfaces), new Handler() {
      public Object handle(Invocation interceptedInvocation) {
        return object;
      }
    });
    assertSame(object, ((Foo) proxy).foo());
  }

  @Test
  public void should_throw_throwable_from_handler() {
    class Foo {
      public Object foo() {
        return null;
      }
    }
    proxy = proxy(typing(Foo.class, interfaces), new Handler() {
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
    proxy = proxy(typing(Foo.class, interfaces), new Handler() {
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

  private static Set<Class<?>> interfaces(Class<?>... elements) {
    HashSet<Class<?>> interfaces = new HashSet<Class<?>>(Arrays.asList(elements));
    if (interfaces.size() != elements.length) {
      throw new IllegalArgumentException();
    }
    return interfaces;
  }
}

class $PackagePrivateConcreteClass {}

interface $PackagePrivateInterfaceA {}

interface $PackagePrivateInterfaceB {}

interface $PackagePrivateInterfaceC {}
