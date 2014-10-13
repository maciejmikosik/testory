package org.testory;

import static org.junit.Assert.assertEquals;
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

public class Describe_verification {
  private Object object;
  private InvocationMatcher onAnything, onNothing;
  private Mockable mock, never, once, twice, thrice;
  private Object numberMatcher;
  private Invocation invocation;

  @Before
  public void before() {
    object = newObject("object");
    onAnything = new InvocationMatcher() {
      public boolean matches(Invocation inv) {
        return true;
      }

      public String toString() {
        return "onAnything";
      }
    };
    onNothing = new InvocationMatcher() {
      public boolean matches(Invocation inv) {
        return false;
      }

      public String toString() {
        return "onNothing";
      }
    };
    mock = mock(Mockable.class);
    never = mock(Mockable.class);
    once = mock(Mockable.class);
    twice = mock(Mockable.class);
    thrice = mock(Mockable.class);
    once.invoke();
    twice.invoke();
    twice.invoke();
    thrice.invoke();
    thrice.invoke();
    thrice.invoke();
  }

  @Test
  public void verification_requires_one_call() {
    try {
      thenCalled(onInstance(never));
      fail();
    } catch (TestoryAssertionError e) {}
    thenCalled(onInstance(once));
    try {
      thenCalled(onInstance(twice));
      fail();
    } catch (TestoryAssertionError e) {}
    try {
      thenCalled(onInstance(thrice));
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void verification_requires_number_of_calls() {
    try {
      thenCalledTimes(2, onInstance(never));
      fail();
    } catch (TestoryAssertionError e) {}
    try {
      thenCalledTimes(2, onInstance(once));
      fail();
    } catch (TestoryAssertionError e) {}
    thenCalledTimes(2, onInstance(twice));
    try {
      thenCalledTimes(2, onInstance(thrice));
      fail();
    } catch (TestoryAssertionError e) {}

    thenCalledTimes(0, onInstance(never));
    try {
      thenCalledTimes(0, onInstance(once));
      fail();
    } catch (TestoryAssertionError e) {}
    try {
      thenCalledTimes(0, onInstance(twice));
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void verification_requires_matching_number_of_calls() {
    numberMatcher = number(1, 2);
    try {
      thenCalledTimes(numberMatcher, onInstance(never));
      fail();
    } catch (TestoryAssertionError e) {}
    thenCalledTimes(numberMatcher, onInstance(once));
    thenCalledTimes(numberMatcher, onInstance(twice));
    try {
      thenCalledTimes(numberMatcher, onInstance(thrice));
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void verification_prints_failed_expectation() {
    try {
      thenCalled(onNothing);
      fail();
    } catch (TestoryAssertionError e) {
      assertTrue(e.getMessage(), e.getMessage().contains("\n" //
          + "  expected called times 1\n" //
          + "    " + onNothing + "\n" //
      ));
    }

    try {
      thenCalledTimes(3, onNothing);
      fail();
    } catch (TestoryAssertionError e) {
      assertTrue(e.getMessage(), e.getMessage().contains("\n" //
          + "  expected called times 3\n" //
          + "    " + onNothing + "\n" //
      ));
    }

    numberMatcher = number(2);
    try {
      thenCalledTimes(numberMatcher, onNothing);
      fail();
    } catch (TestoryAssertionError e) {
      assertTrue(e.getMessage(), e.getMessage().contains("\n" //
          + "  expected called times " + numberMatcher + "\n" //
          + "    " + onNothing + "\n" //
      ));
    }
  }

  @Test
  public void verification_prints_actual_number_of_invocations() {
    try {
      thenCalled(onNothing);
      fail();
    } catch (TestoryAssertionError e) {
      assertTrue(e.getMessage(), e.getMessage().contains("" //
          + "  but called" + "\n" //
          + "    times " + 0 + "\n" //
      ));
    }

    try {
      thenCalledTimes(3, onInstance(once));
      fail();
    } catch (TestoryAssertionError e) {
      assertTrue(e.getMessage(), e.getMessage().contains("" //
          + "  but called" + "\n" //
          + "    times " + 1 + "\n" //
      ));
    }

    numberMatcher = number(2);
    try {
      thenCalledTimes(numberMatcher, onInstance(thrice));
      fail();
    } catch (TestoryAssertionError e) {
      assertTrue(e.getMessage(), e.getMessage().contains("" //
          + "  but called" + "\n" //
          + "    times " + 3 + "\n" //
      ));
    }
  }

  @Test
  public void verification_does_not_affect_following_verifications() {
    thenCalled(onInstance(once));
    thenCalled(onInstance(once));
    thenCalledTimes(2, onInstance(twice));
    thenCalledTimes(2, onInstance(twice));
    thenCalledTimes(number(2), onInstance(twice));
    thenCalledTimes(number(2), onInstance(twice));
  }

  @Test
  public void verification_includes_invocations_inside_when() {
    when(mock.invoke());
    thenCalled(onInstance(mock));
  }

  @Test
  public void verification_includes_invocations_before_when() {
    mock.invoke();
    when("do something");
    thenCalled(onInstance(mock));
  }

  @Test
  public void verificaiton_includes_invocations_before_when_closure() {
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
  public void checks_that_number_of_calls_is_not_negative() {
    try {
      thenCalledTimes(-1, onNothing);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void checks_that_number_matcher_is_matcher() {
    try {
      thenCalledTimes(new Object(), onAnything);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void checks_that_number_matcher_is_not_null() {
    try {
      thenCalledTimes(null, onAnything);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void checks_that_invocation_matcher_is_not_null() {
    try {
      thenCalled((InvocationMatcher) null);
      fail();
    } catch (TestoryException e) {}

    try {
      thenCalledTimes(1, (InvocationMatcher) null);
      fail();
    } catch (TestoryException e) {}

    try {
      thenCalledTimes(number(1), (InvocationMatcher) null);
      fail();
    } catch (TestoryException e) {}
  }

  private static InvocationMatcher onInstance(final Object mock) {
    return new InvocationMatcher() {
      public boolean matches(Invocation invocation) {
        return invocation.instance == mock;
      }
    };
  }

  private static Object number(final Integer... numbers) {
    return new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object item) {
        return Arrays.asList(numbers).contains(item);
      }

      public String toString() {
        return "number(" + Arrays.toString(numbers) + ")";
      }
    };
  }

  private static abstract class Mockable {
    abstract Object invoke();

    abstract void acceptObject(Object o);
  }
}
