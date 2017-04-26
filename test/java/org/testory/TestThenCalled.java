package org.testory;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.testing.HamcrestMatchers.hasMessage;
import static org.testory.testing.HamcrestMatchers.hasMessageContaining;

import org.junit.Before;
import org.junit.Test;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class TestThenCalled {
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
      assertThat(e, hasMessageContaining("expected called times 1\n"));
    }
  }

  @Test
  public void checks_that_invocation_matcher_is_not_null() {
    try {
      thenCalled((InvocationMatcher) null);
      fail();
    } catch (TestoryException e) {
      assertThat(e, hasMessage("expected not null"));
    }
  }

  @Test
  public void checks_that_mock_is_not_null() {
    try {
      thenCalled((Object) null);
      fail();
    } catch (TestoryException e) {
      assertThat(e, hasMessage("expected not null"));
    }
  }

  @Test
  public void checks_that_mock_is_mock() {
    try {
      thenCalled(new Object());
      fail();
    } catch (TestoryException e) {
      assertThat(e, hasMessage("expected mock"));
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
