package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenCalledTimes;
import static org.testory.Testory.when;
import static org.testory.test.Testilities.newObject;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class describe_verification {
  private Object object;
  private Mockable mock;
  private Invocation invocation;

  @Before
  public void before() {
    object = newObject("object");
    mock = mock(Mockable.class);
  }

  @Test
  public void chained_verification_does_not_log_invocation() {
    mock.invoke();
    thenCalled(mock).invoke();
    thenCalled(mock).invoke();
  }

  @Test
  public void includes_invocations_inside_when() {
    when(mock.invoke());
    thenCalled(onInstance(mock));
  }

  @Test
  public void includes_invocations_before_when() {
    mock.invoke();
    when("do something");
    thenCalled(onInstance(mock));
  }

  @Test
  public void includes_invocations_before_when_closure() {
    mock.invoke();
    when(new Closure() {
      public Object invoke() throws Throwable {
        return null;
      }
    });
    thenCalled(onInstance(mock));
  }

  @Test
  public void invocation_matcher_matches_invocation_on_mock() throws NoSuchMethodException {
    mock.acceptObject(object);
    thenCalled(new InvocationMatcher() {
      public boolean matches(Invocation inv) {
        if (inv.instance == mock) {
          invocation = inv;
          return true;
        }
        return false;
      }
    });
    assertSame(mock, invocation.instance);
    assertEquals(Mockable.class.getDeclaredMethod("acceptObject", Object.class), invocation.method);
    assertEquals(Arrays.asList(object), invocation.arguments);
  }

  @Test
  public void failure_prints_actual_invocations() {
    mock.invoke();
    mock.acceptObject(object);
    try {
      thenCalledTimes(0, onInstance(mock));
      fail();
    } catch (TestoryAssertionError e) {
      assertTrue(e.getMessage(), e.getMessage().contains("" //
          + "  actual invocations\n" //
      ));
      assertTrue(e.getMessage(), e.getMessage().contains("" //
          + "    " + mock.toString() + ".invoke()\n" //
          + "    " + mock.toString() + ".acceptObject(object)\n" //
      ));
    }
  }

  @Test
  public void failure_prints_special_message_if_no_actual_invocations() {
    when("");
    when("");
    mock = mock(Mockable.class);
    try {
      thenCalledTimes(1, onInstance(mock));
      fail();
    } catch (TestoryAssertionError e) {
      assertTrue(e.getMessage(), e.getMessage().contains("" //
          + "  actual invocations\n" //
          + "    none\n" //
      ));
    }
  }

  @Test
  public void printing_failure_does_not_log_invocation_on_tostring() {
    mock.invoke();
    mock.acceptObject(object);
    try {
      thenCalledTimes(0, onInstance(mock));
      fail();
    } catch (TestoryAssertionError e) {}
    try {
      thenCalledTimes(0, onInstance(mock));
      fail();
    } catch (TestoryAssertionError e) {
      assertFalse(e.getMessage(), e.getMessage().contains(mock.toString() + ".toString()"));
    }
  }

  private static InvocationMatcher onInstance(final Object mock) {
    return new InvocationMatcher() {
      public boolean matches(Invocation invocation) {
        return invocation.instance == mock;
      }

      public String toString() {
        return "onInstance(" + mock + ")";
      }
    };
  }

  private static abstract class Mockable {
    abstract Object invoke();

    abstract void acceptObject(Object o);
  }
}
