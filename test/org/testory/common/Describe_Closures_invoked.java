package org.testory.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testory.common.Closures.invoked;
import static org.testory.test.TestUtils.newObject;
import static org.testory.test.TestUtils.newThrowable;

import org.junit.Before;
import org.junit.Test;

public class Describe_Closures_invoked {
  private Closure closure, invoked;
  private Object object;
  private Throwable throwable;

  @Before
  public void before() {
    object = newObject("object");
    throwable = newThrowable("throwable");
    closure = mock(Closure.class);
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
    invoked(closure);
    verify(closure).invoke();
  }

  @Test
  public void should_not_invoke_closure_second_time() throws Throwable {
    invoked = invoked(closure);
    invoked.invoke();
    verify(closure).invoke();
  }
}
