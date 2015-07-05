package org.testory;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalledNever;

import org.junit.Before;
import org.junit.Test;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class test_verifying_never {
  private Mockable mock;

  @Before
  public void before() {
    mock = mock(Mockable.class);
  }

  @Test
  public void asserts_no_calls() {
    thenCalledNever(onInstance(mock));
  }

  @Test
  public void asserts_no_calls_chained() {
    thenCalledNever(mock).invoke();
  }

  @Test
  public void fails_if_unexpected_call() {
    mock.invoke();
    try {
      thenCalledNever(onInstance(mock));
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void fails_if_unexpected_call_chained() {
    mock.invoke();
    try {
      thenCalledNever(mock).invoke();
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void failure_prints_expected_number_of_calls() {
    mock.invoke();
    try {
      thenCalledNever(onInstance(mock));
      fail();
    } catch (TestoryAssertionError e) {
      assertTrue(e.getMessage(), e.getMessage().contains(""
          + "expected called times " + 0 + "\n"));
    }
  }

  @Test
  public void checks_that_invocation_matcher_is_not_null() {
    try {
      thenCalledNever((InvocationMatcher) null);
      fail();
    } catch (TestoryException e) {
      assertTrue(e.getMessage(), e.getMessage().contains("invocationMatcher != null"));
    }
  }

  @Test
  public void checks_that_mock_is_not_null() {
    try {
      thenCalledNever((Object) null);
      fail();
    } catch (TestoryException e) {
      assertTrue(e.getMessage(), e.getMessage().contains("mock != null"));
    }
  }

  @Test
  public void checks_that_mock_is_mock() {
    try {
      thenCalledNever(new Object());
      fail();
    } catch (TestoryException e) {
      assertTrue(e.getMessage(), e.getMessage().contains("isMock(mock)"));
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
