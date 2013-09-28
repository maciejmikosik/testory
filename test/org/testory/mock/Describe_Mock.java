package org.testory.mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.mock.Invocation.invocation;
import static org.testory.mock.Mocks.mock;
import static org.testory.mock.Typing.typing;
import static org.testory.test.TestUtils.newObject;
import static org.testory.test.TestUtils.newThrowable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

public class Describe_Mock {
  private Handler handler;
  private Typing typing;
  private Invocation savedInvocation;
  private Object object, mock;
  private Method method;
  private Throwable throwable;

  @Before
  public void before() throws NoSuchMethodException {
    method = Object.class.getDeclaredMethod("toString");
    typing = typing(Object.class, interfaces());
    handler = new Handler() {
      public Object handle(Invocation invocation) {
        return null;
      }
    };
    object = newObject("object");
    throwable = newThrowable("throwable");
  }

  @Test
  public void should_create_mock_extending_object() {
    typing = typing(Object.class, interfaces());
    mock = mock(typing, handler);
    assertTrue(mock instanceof Object);
  }

  @Test
  public void should_create_mock_extending_concrete_class() {
    typing = typing($ConcreteClass.class, interfaces());
    mock = mock(typing, handler);
    assertTrue(mock instanceof $ConcreteClass);
  }

  @Test
  public void should_create_mock_extending_package_private_concrete_class() {
    typing = typing($PackagePrivateConcreteClass.class, interfaces());
    mock = mock(typing, handler);
    assertTrue(mock instanceof $PackagePrivateConcreteClass);
  }

  @Test
  public void should_create_mock_extending_nested_in_method_concrete_class() {
    class NestedConcreteClass {}
    typing = typing(NestedConcreteClass.class, interfaces());
    mock = mock(typing, handler);
    assertTrue(mock instanceof NestedConcreteClass);
  }

  @Test
  public void should_create_mock_extending_abstract_class_with_abstract_method() {
    typing = typing($AbstractClassWithAbstractMethod.class, interfaces());
    mock = mock(typing, handler);
    assertTrue(mock instanceof $AbstractClassWithAbstractMethod);
  }

  @Test
  public void should_create_mock_extending_abstract_class_with_protected_abstract_method() {
    typing = typing($AbstractClassWithProtectedAbstractMethod.class, interfaces());
    mock = mock(typing, handler);
    assertTrue(mock instanceof $AbstractClassWithProtectedAbstractMethod);
  }

  @Test
  public void should_create_mock_implementing_many_interfaces() {
    typing = typing(Object.class,
        interfaces($InterfaceA.class, $InterfaceB.class, $InterfaceC.class));
    mock = mock(typing, handler);
    assertTrue(mock instanceof $InterfaceA);
    assertTrue(mock instanceof $InterfaceB);
    assertTrue(mock instanceof $InterfaceC);
  }

  // TODO fix: mock of package private interfaces
  @Ignore
  @Test
  public void should_create_mock_implementing_many_package_private_interfaces() {
    typing = typing(
        Object.class,
        interfaces($PackagePrivateInterfaceA.class, $PackagePrivateInterfaceB.class,
            $PackagePrivateInterfaceC.class));
    mock = mock(typing, handler);
    assertTrue(mock instanceof $PackagePrivateInterfaceA);
    assertTrue(mock instanceof $PackagePrivateInterfaceB);
    assertTrue(mock instanceof $PackagePrivateInterfaceC);
  }

  @Test
  public void should_create_mock_extending_type_of_other_mock() {
    typing = typing($ConcreteClass.class, interfaces($InterfaceA.class));
    mock = mock(typing, handler);
    mock = mock(typing(mock.getClass(), interfaces($InterfaceB.class)), handler);
    assertTrue(mock instanceof $ConcreteClass);
    assertTrue(mock instanceof $InterfaceA);
    assertTrue(mock instanceof $InterfaceB);
  }

  @Test
  public void should_create_mock_extending_type_of_other_mock_extending_object() {
    typing = typing(Object.class, interfaces());
    mock = mock(typing, handler);
    mock = mock(typing(mock.getClass(), interfaces($InterfaceA.class)), handler);
    assertTrue(mock instanceof $InterfaceA);
  }

  @Test
  public void should_create_mock_extending_concrete_class_with_private_default_constructor() {
    typing = typing($ConcreteClassWithPrivateDefaultConstructor.class, interfaces());
    mock = mock(typing, handler);
    assertTrue(mock instanceof $ConcreteClassWithPrivateDefaultConstructor);
  }

  @Test
  public void should_create_mock_extending_concrete_class_with_private_constructor_with_arguments() {
    typing = typing($ConcreteClassWithPrivateConstructorWithArguments.class, interfaces());
    mock = mock(typing, handler);
    assertTrue(mock instanceof $ConcreteClassWithPrivateConstructorWithArguments);
  }

  @Test
  public void should_create_mock_extending_type_of_other_mock_and_implementing_duplicated_interface() {
    typing = typing($ConcreteClass.class, interfaces($InterfaceA.class));
    mock = mock(typing, handler);
    mock = mock(typing(mock.getClass(), interfaces($InterfaceA.class)), handler);
    assertTrue(mock instanceof $ConcreteClass);
    assertTrue(mock instanceof $InterfaceA);
  }

  @Test
  public void should_create_mock_with_duplicated_interfaces() {
    class Superclass implements $InterfaceA {}
    typing = typing(Superclass.class, interfaces($InterfaceA.class));
    mock = mock(typing, handler);
    assertTrue(mock instanceof Superclass);
    assertTrue(mock instanceof $InterfaceA);
  }

  @Test
  public final void should_intercept_invocation() throws NoSuchMethodException {
    class Foo {
      public Object foo(Object foo) {
        return null;
      }
    }
    typing = typing(Foo.class, interfaces());
    mock = mock(typing, handlerSavingInvocation());
    method = Foo.class.getDeclaredMethod("foo", Object.class);
    ((Foo) mock).foo(object);
    assertEquals(invocation(method, mock, Arrays.asList(object)), savedInvocation);
  }

  @Test
  public void should_intercept_equals() throws NoSuchMethodException {
    mock = mock(typing, handlerSavingInvocation());
    method = Object.class.getDeclaredMethod("equals", Object.class);
    mock.equals(object);
    assertEquals(invocation(method, mock, Arrays.asList(object)), savedInvocation);
  }

  @Test
  public void should_intercept_to_string() throws NoSuchMethodException {
    mock = mock(typing, handlerSavingInvocation());
    method = Object.class.getDeclaredMethod("toString");
    mock.toString();
    assertEquals(invocation(method, mock, Arrays.asList()), savedInvocation);
  }

  @Test
  public void should_intercept_clone() throws NoSuchMethodException {
    typing = typing($ConcreteClassWithClone.class, interfaces());
    mock = mock(typing, handlerSavingInvocation());
    method = $ConcreteClassWithClone.class.getDeclaredMethod("clone");
    (($ConcreteClassWithClone) mock).clone();
    assertEquals(invocation(method, mock, Arrays.asList()), savedInvocation);
  }

  @Test
  public void should_not_intercept_finalize() {
    handler = Mockito.mock(Handler.class);
    typing = typing($ConcreteClassWithFinalize.class, interfaces());
    mock = mock(typing, handler);
    (($ConcreteClassWithFinalize) mock).finalize();
    Mockito.verifyZeroInteractions(handler);
  }

  @Test
  public void should_intercept_package_private_method() throws NoSuchMethodException {
    typing = typing($ConcreteClassWithPackagePrivateMethod.class, interfaces());
    mock = mock(typing, handlerSavingInvocation());
    method = $ConcreteClassWithPackagePrivateMethod.class.getDeclaredMethod("packagePrivateMethod");
    (($ConcreteClassWithPackagePrivateMethod) mock).packagePrivateMethod();
    assertEquals(invocation(method, mock, Arrays.asList()), savedInvocation);
  }

  @Test
  public void should_intercept_protected_abstract_method() throws NoSuchMethodException {
    typing = typing($AbstractClassWithProtectedAbstractMethod.class, interfaces());
    mock = mock(typing, handlerSavingInvocation());
    method = $AbstractClassWithProtectedAbstractMethod.class.getDeclaredMethod("abstractMethod");
    (($AbstractClassWithProtectedAbstractMethod) mock).abstractMethod();
    assertEquals(invocation(method, mock, Arrays.asList()), savedInvocation);
  }

  @Test
  public void should_return_result_from_handler() {
    class Foo {
      public Object foo() {
        return null;
      }
    }
    mock = mock(typing(Foo.class, interfaces()), new Handler() {
      public Object handle(Invocation interceptedInvocation) {
        return object;
      }
    });
    assertSame(object, ((Foo) mock).foo());
  }

  @Test
  public void should_throw_throwable_from_handler() {
    class Foo {
      public Object foo() {
        return null;
      }
    }
    mock = mock(typing(Foo.class, interfaces()), new Handler() {
      public Object handle(Invocation interceptedInvocation) {
        throw undeclared(throwable);
      }
    });
    try {
      ((Foo) mock).foo();
      fail();
    } catch (Throwable t) {
      assertSame(throwable, t);
    }
  }

  @Test
  public void should_stack_overflow_for_handler_invoking_invocation() {
    mock = mock(typing, new Handler() {
      public Object handle(Invocation interceptedInvocation) {
        try {
          return interceptedInvocation.method.invoke(interceptedInvocation.instance,
              interceptedInvocation.arguments.toArray());
        } catch (InvocationTargetException e) {
          throw undeclared(e.getCause());
        } catch (Exception e) {
          throw undeclared(e);
        }
      }
    });
    try {
      mock.toString();
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
    mock = mock(typing(Foo.class, interfaces()), new Handler() {
      public Object handle(Invocation interceptedInvocation) {
        return null;
      }
    });
    assertEquals(0, ((Foo) mock).foo());
  }

  @Test
  public void should_not_create_mock_extending_final_type() {
    try {
      mock(typing($FinalClass.class, interfaces()), handler);
      fail();
    } catch (IllegalArgumentException e) {}
  }

  @Test
  public final void should_fail_for_null_typing() {
    try {
      mock(null, handler);
      fail();
    } catch (NullPointerException e) {}
  }

  @Test
  public final void should_fail_for_null_handler() {
    try {
      mock(typing, null);
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

  private static RuntimeException undeclared(Throwable throwable) {
    Describe_Mock.<RuntimeException> doThrow(throwable);
    throw new Error("unreachable");
  }

  private static <T extends Throwable> void doThrow(Throwable throwable) throws T {
    throw (T) throwable;
  }
}

class $PackagePrivateConcreteClass {}

interface $PackagePrivateInterfaceA {}

interface $PackagePrivateInterfaceB {}

interface $PackagePrivateInterfaceC {}
