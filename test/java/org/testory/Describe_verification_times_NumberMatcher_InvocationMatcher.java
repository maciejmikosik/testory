package org.testory;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalledTimes;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.testory.proxy.Invocation;

public class Describe_verification_times_NumberMatcher_InvocationMatcher {
  private Mockable mock;

  @Before
  public void before() {
    mock = mock(Mockable.class);
  }

  @Test
  public void asserts_expected_number_of_calls() {
    mock.invoke();
    mock.invoke();
    mock.invoke();
    thenCalledTimes(number(3), onInstance(mock));
  }

  @Test
  public void asserts_expected_number_of_calls_chained() {
    mock.invoke();
    mock.invoke();
    mock.invoke();
    thenCalledTimes(number(3), mock).invoke();
  }

  @Test
  public void fails_if_unexpected_number_of_calls() {
    mock.invoke();
    mock.invoke();
    try {
      thenCalledTimes(number(3), onInstance(mock));
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void fails_if_unexpected_number_of_calls_chained() {
    mock.invoke();
    mock.invoke();
    try {
      thenCalledTimes(number(3), mock).invoke();
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void failure_prints_expected_number_of_calls() {
    try {
      thenCalledTimes(number(3), onInstance(mock));
      fail();
    } catch (TestoryAssertionError e) {
      assertTrue(e.getMessage(), e.getMessage().contains("" //
          + "expected called times " + number(3) + "\n" //
      ));
    }
  }

  @Test
  public void checks_that_number_matcher_is_matcher() {
    try {
      thenCalledTimes(new Object(), onInstance(mock));
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void checks_that_number_matcher_is_not_null() {
    try {
      thenCalledTimes(null, onInstance(mock));
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void checks_that_invocation_matcher_is_not_null() {
    try {
      thenCalledTimes(number(1), (InvocationMatcher) null);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void checks_that_mock_is_not_null() {
    try {
      thenCalledTimes(number(1), (Object) null);
      fail();
    } catch (TestoryException e) {}
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
    void invoke() {}
  }
}
