package org.testory.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.common.Closures.invoked;
import static org.testory.test.TestUtils.newObject;
import static org.testory.test.TestUtils.newThrowable;

import org.junit.Before;
import org.junit.Test;

public class Describe_Closures_invoked {
  private Closure closure, invoked;
  private Object object;
  private Throwable throwable;
  private int counter;

  @Before
  public void before() {
    object = newObject("object");
    throwable = newThrowable("throwable");
  }

  @Test
  public void should_handle_closure_returning() throws Throwable {
    closure = new Closure() {
      public Object invoke() {
        return object;
      }
    };
    invoked = invoked(closure);
    assertEquals(object, invoked.invoke());
  }

  @Test
  public void should_handle_closure_throwing() {
    closure = new Closure() {
      public Object invoke() throws Throwable {
        throw throwable;
      }
    };
    invoked = invoked(closure);
    try {
      invoked.invoke();
      fail();
    } catch (Throwable e) {
      assertEquals(throwable, e);
    }
  }

  @Test
  public void should_invoke_closure_once() throws Throwable {
    closure = new Closure() {
      public Object invoke() {
        return counter++;
      }
    };
    invoked(closure);
    assertEquals(1, counter);
  }

  @Test
  public void should_not_invoke_closure_second_time() throws Throwable {
    closure = new Closure() {
      public Object invoke() throws Throwable {
        return counter++;
      }
    };
    invoked = invoked(closure);
    invoked.invoke();
    assertEquals(1, counter);
  }
}
