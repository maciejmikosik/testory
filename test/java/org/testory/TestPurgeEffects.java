package org.testory;

import static org.junit.Assert.fail;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenCalledTimes;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;
import static org.testory.testing.Fakes.newObject;
import static org.testory.testing.Purging.triggerPurge;

import org.junit.Before;
import org.junit.Test;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class TestPurgeEffects {
  private Mockable mock;
  private Object object;

  @Before
  public void before() {
    triggerPurge();
    mock = mock(Mockable.class);
    object = newObject("object");
  }

  @Test
  public void inspection_cannot_be_used() {
    when(object);
    triggerPurge();
    try {
      thenReturned(object);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void mock_cannot_be_invoked() {
    triggerPurge();
    try {
      mock.getObject();
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void mock_cannot_be_stubbed() {
    triggerPurge();
    try {
      given(willReturn(object), mock);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void mock_cannot_be_verified() {
    triggerPurge();
    try {
      thenCalled(mock);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void invocation_cannot_be_verified() {
    mock.getObject();
    triggerPurge();
    thenCalledTimes(0, onInstance(mock));
  }

  private static InvocationMatcher onInstance(final Object mock) {
    return new InvocationMatcher() {
      public boolean matches(Invocation invocation) {
        return invocation.instance == mock;
      }
    };
  }

  private static abstract class Mockable {
    abstract Object getObject();
  }
}
