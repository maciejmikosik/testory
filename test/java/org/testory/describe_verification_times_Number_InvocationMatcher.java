package org.testory;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalledTimes;

import org.junit.Before;
import org.junit.Test;
import org.testory.proxy.Invocation;

public class describe_verification_times_Number_InvocationMatcher {
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
    thenCalledTimes(3, onInstance(mock));
  }

  @Test
  public void asserts_expected_number_of_calls_chained() {
    mock.invoke();
    mock.invoke();
    mock.invoke();
    thenCalledTimes(3, mock).invoke();
  }

  @Test
  public void fails_if_unexpected_number_of_calls() {
    mock.invoke();
    mock.invoke();
    try {
      thenCalledTimes(3, onInstance(mock));
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void fails_if_unexpected_number_of_calls_chained() {
    mock.invoke();
    mock.invoke();
    try {
      thenCalledTimes(3, mock).invoke();
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void failure_prints_expected_number_of_calls() {
    try {
      thenCalledTimes(3, onInstance(mock));
      fail();
    } catch (TestoryAssertionError e) {
      assertTrue(e.getMessage(), e.getMessage().contains("" //
          + "expected called times " + 3 + "\n" //
      ));
    }
  }

  @Test
  public void checks_that_number_of_calls_is_not_negative() {
    try {
      thenCalledTimes(-1, onInstance(mock));
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void checks_that_invocation_matcher_is_not_null() {
    try {
      thenCalledTimes(1, (InvocationMatcher) null);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void checks_that_mock_is_not_null() {
    try {
      thenCalledTimes(1, (Object) null);
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

  private static abstract class Mockable {
    void invoke() {}
  }
}
