package org.testory;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.when;
import static org.testory.testing.Fakes.newObject;
import static org.testory.testing.HamcrestMatchers.hasMessageContaining;
import static org.testory.testing.Purging.triggerPurge;

import org.junit.Before;
import org.junit.Test;
import org.testory.common.Closure;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class TestThenCalledAllOverloadings {
  private Object object;
  private Mockable mock;
  private Invocation invocation;

  @Before
  public void before() {
    triggerPurge();
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
  public void includes_invocations_inside_when_closure() {
    when(new Closure() {
      public Object invoke() throws Throwable {
        mock.invoke();
        return null;
      }
    });
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
    assertEquals(asList(object), invocation.arguments);
  }

  @Test
  public void failure_prints_actual_number_of_calls() {
    mock.invoke();
    mock.invoke();
    mock.invoke();
    try {
      thenCalled(onInstance(mock));
      fail();
    } catch (TestoryAssertionError e) {
      assertThat(e, hasMessageContaining(""
          + "  but called\n"
          + "    times 3\n"));
    }
  }

  @Test
  public void failure_prints_actual_invocations() {
    mock.invoke();
    mock.acceptObject(object);
    mock.acceptObjects(object, object);
    try {
      thenCalled(onInstance(mock));
      fail();
    } catch (TestoryAssertionError e) {
      assertThat(e, hasMessageContaining(""
          + format("  actual invocations\n")
          + format("    %s.invoke()\n", mock)
          + format("    %s.acceptObject(%s)\n", mock, object)
          + format("    %s.acceptObjects(%s, %s)\n", mock, object, object)));
    }
  }

  @Test
  public void failure_prints_actual_invocations_with_array_arguments() {
    mock.invoke();
    mock.acceptObject(new Object[][] { { object } });
    try {
      thenCalled(onInstance(mock));
      fail();
    } catch (TestoryAssertionError e) {
      assertThat(e, hasMessageContaining(
          format("    %s.acceptObject([[%s]])\n", mock, object)));
    }
  }

  @Test
  public void failure_prints_special_message_if_no_actual_invocations() {
    try {
      thenCalled(onInstance(mock));
      fail();
    } catch (TestoryAssertionError e) {
      assertThat(e, hasMessageContaining(""
          + "  actual invocations\n"
          + "    none\n"));
    }
  }

  @Test
  public void printing_failure_does_not_log_invocation_on_tostring() {
    mock.invoke();
    mock.invoke();
    try {
      thenCalled(onInstance(mock));
      fail();
    } catch (TestoryAssertionError e) {}
    try {
      thenCalled(onInstance(mock));
      fail();
    } catch (TestoryAssertionError e) {
      assertThat(e, not(hasMessageContaining(mock + ".toString()")));
    }
  }

  private static InvocationMatcher onInstance(final Object mock) {
    return new InvocationMatcher() {
      public boolean matches(Invocation invocation) {
        return invocation.instance == mock;
      }

      public String toString() {
        return format("onInstance(%s)", mock);
      }
    };
  }

  private static abstract class Mockable {
    abstract Object invoke();

    abstract void acceptObject(Object o);

    abstract void acceptObjects(Object o, Object o2);
  }
}
