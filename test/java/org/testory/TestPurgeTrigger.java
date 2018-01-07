package org.testory;

import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.then;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenCalledNever;
import static org.testory.Testory.when;
import static org.testory.testing.Purging.triggerPurge;

import org.junit.Before;
import org.junit.Test;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class TestPurgeTrigger {
  private Object mock;

  @Before
  public void before() {
    triggerPurge();
    mock = mock(Object.class);
    mock.toString();
  }

  @Test
  public void by_then_given() {
    then(true);
    given(true);
    assertTriggered(true);
  }

  @Test
  public void by_then_when() {
    then(true);
    when(true);
    assertTriggered(true);
  }

  @Test
  public void not_by_then_then() {
    then(true);
    then(true);
    assertTriggered(false);
  }

  @Test
  public void not_by_when() {
    when(true);
    assertTriggered(false);
  }

  @Test
  public void by_when_when() {
    when(true);
    when(true);
    assertTriggered(true);
  }

  @Test
  public void not_by_whenc() {
    when(new Object()).toString();
    assertTriggered(false);
  }

  @Test
  public void by_whenc_whenc() {
    when(new Object()).toString();
    when(new Object()).toString();
    assertTriggered(true);
  }

  @Test
  public void not_by_given_given_when_then_then() {
    given(true);
    given(true);
    when(true);
    then(true);
    then(true);
    assertTriggered(false);
  }

  private void assertTriggered(boolean triggered) {
    if (triggered) {
      thenCalledNever(anyInvocation());
    } else {
      thenCalled(anyInvocation());
    }
  }

  private static InvocationMatcher anyInvocation() {
    return new InvocationMatcher() {
      public boolean matches(Invocation invocation) {
        return true;
      }
    };
  }
}
