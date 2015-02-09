package org.testory;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;

import org.junit.Before;
import org.junit.Test;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class describe_verifying {
  private Mockable mock;

  @Before
  public void before() {
    mock = mock(Mockable.class);
  }

  @Test
  public void asserts_exactly_one_call() {
    mock.invoke();
    thenCalled(onInstance(mock));
  }

  @Test
  public void asserts_exactly_one_call_chained() {
    mock.invoke();
    thenCalled(mock).invoke();
  }

  @Test
  public void fails_if_no_calls() {
    try {
      thenCalled(onInstance(mock));
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void fails_if_no_calls_chained() {
    try {
      thenCalled(mock).invoke();
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void fails_if_more_calls() {
    mock.invoke();
    mock.invoke();
    mock.invoke();
    try {
      thenCalled(onInstance(mock));
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void fails_if_more_calls_chained() {
    mock.invoke();
    mock.invoke();
    mock.invoke();
    try {
      thenCalled(mock).invoke();
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void failure_prints_that_exactly_one_call_was_expected() {
    try {
      thenCalled(onInstance(mock));
      fail();
    } catch (TestoryAssertionError e) {
      assertTrue(e.getMessage(), e.getMessage().contains("" //
          + "expected called times 1\n" //
      ));
    }
  }

  @Test
  public void checks_that_invocation_matcher_is_not_null() {
    try {
      thenCalled((InvocationMatcher) null);
      fail();
    } catch (TestoryException e) {
      assertTrue(e.getMessage(), e.getMessage().contains("invocationMatcher != null"));
    }
  }

  @Test
  public void checks_that_mock_is_not_null() {
    try {
      thenCalled((Object) null);
      fail();
    } catch (TestoryException e) {
      assertTrue(e.getMessage(), e.getMessage().contains("mock != null"));
    }
  }

  @Test
  public void checks_that_mock_is_mock() {
    try {
      thenCalled(new Object());
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
