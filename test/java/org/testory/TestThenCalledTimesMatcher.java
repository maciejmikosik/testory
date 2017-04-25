package org.testory;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalledTimes;
import static org.testory.testing.DynamicMatchers.number;
import static org.testory.testing.HamcrestMatchers.hasMessageContaining;

import org.junit.Before;
import org.junit.Test;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class TestThenCalledTimesMatcher {
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
      assertThat(e, hasMessageContaining(""
          + "expected called times " + number(3) + "\n"));
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
    } catch (TestoryException e) {
      assertThat(e, hasMessageContaining("invocationMatcher != null"));
    }
  }

  @Test
  public void checks_that_mock_is_not_null() {
    try {
      thenCalledTimes(number(1), (Object) null);
      fail();
    } catch (TestoryException e) {
      assertThat(e, hasMessageContaining("mock != null"));
    }
  }

  @Test
  public void checks_that_mock_is_mock() {
    try {
      thenCalledTimes(number(1), new Object());
      fail();
    } catch (TestoryException e) {
      assertThat(e, hasMessageContaining("isMock(mock)"));
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
    void invoke() {}
  }
}
