package org.testory;

import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.then;
import static org.testory.Testory.thenCalledNever;
import static org.testory.Testory.when;
import static org.testory.testing.Purging.triggerPurge;

import org.junit.Before;
import org.junit.Test;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class TestPurgeScope {
  private Object mock;

  @Before
  public void before() {
    triggerPurge();
  }

  @Test
  public void given_purges_invocation() {
    mock = mock(Object.class);
    then(true);
    mock.hashCode();

    given(true);
    when(true);
    thenCalledNever(anyInvocation());
  }

  @Test
  public void given_purges_mocking_and_invocation() {
    then(true);
    mock = mock(Object.class);
    mock.hashCode();

    given(true);
    when(true);
    thenCalledNever(anyInvocation());
  }

  private static InvocationMatcher anyInvocation() {
    return new InvocationMatcher() {
      public boolean matches(Invocation invocation) {
        return true;
      }
    };
  }
}
