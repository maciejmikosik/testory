package org.testory;

import static java.lang.String.format;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalledNever;
import static org.testory.testing.HamcrestMatchers.hasMessage;
import static org.testory.testing.HamcrestMatchers.hasMessageContaining;

import org.junit.Before;
import org.junit.Test;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class TestThenCalledNever {
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
      assertThat(e, hasMessageContaining("expected called times 0\n"));
    }
  }

  @Test
  public void checks_that_invocation_matcher_is_not_null() {
    try {
      thenCalledNever((InvocationMatcher) null);
      fail();
    } catch (TestoryException e) {
      assertThat(e, hasMessage("expected not null"));
    }
  }

  @Test
  public void checks_that_mock_is_not_null() {
    try {
      thenCalledNever((Object) null);
      fail();
    } catch (TestoryException e) {
      assertThat(e, hasMessage("expected not null"));
    }
  }

  @Test
  public void checks_that_mock_is_mock() {
    try {
      thenCalledNever(new Object());
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
        return format("onInstance(%s)", mock);
      }
    };
  }

  private static abstract class Mockable {
    void invoke() {}
  }
}
