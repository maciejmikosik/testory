package org.testory.proxy.extra;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.testory.proxy.Invocation.invocation;
import static org.testory.proxy.extra.Overrider.overrider;
import static org.testory.proxy.proxer.CglibProxer.cglibProxer;
import static org.testory.testing.Fakes.newObject;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.ProxyException;

public class TestOverrider {
  private Overrider overrider;
  private Foo instance, overridden;
  private Object argument, result;
  private Invocation invoked;

  @Before
  public void before() {
    overrider = overrider(cglibProxer());
    instance = new Foo("instance");
    argument = newObject("argument");
    result = newObject("result");
  }

  @Test
  public void invokes_handler() {
    overridden = overrider.override(instance, new Handler() {
      public Object handle(Invocation invocation) {
        invoked = invocation;
        return null;
      }
    });

    overridden.invoke(argument);

    assertEquals(
        invocation(Foo.getInvokeMethod(), instance, asList(argument)),
        invoked);
  }

  @Test
  public void returns_from_handler() {
    overridden = overrider.override(instance, new Handler() {
      public Object handle(Invocation invocation) {
        return result;
      }
    });

    assertSame(result, overridden.invoke(argument));
  }

  @Test
  public void throws_from_handler() {
    overridden = overrider.override(instance, new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        throw new FooException();
      }
    });

    try {
      overridden.invoke(argument);
      fail();
    } catch (FooException e) {}
  }

  private static class Foo {
    private final String name;

    public Foo(String name) {
      this.name = name;

    }

    public Object invoke(Object argument) {
      return argument;
    }

    public static Method getInvokeMethod() {
      try {
        return Foo.class.getMethod("invoke", Object.class);
      } catch (NoSuchMethodException e) {
        throw new LinkageError("", e);
      }
    }

    public String toString() {
      return name;
    }
  }

  @Test
  public void proxer_cannot_be_null() {
    try {
      overrider(null);
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void cannot_override_null_instance() {
    try {
      overrider.override(null, new Handler() {
        public Object handle(Invocation invocation) throws Throwable {
          return null;
        }
      });
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void cannot_override_null_handler() {
    try {
      overrider.override(instance, null);
      fail();
    } catch (ProxyException e) {}
  }

  private static class FooException extends RuntimeException {}
}
