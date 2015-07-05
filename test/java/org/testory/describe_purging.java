package org.testory;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenCalledTimes;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;
import static org.testory.testing.Testilities.newObject;

import org.junit.Before;
import org.junit.Test;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class describe_purging {
  private Mockable mock;
  private Object object;

  @Before
  public void before() {
    mock = mock(Mockable.class);
    object = newObject("object");
  }

  @Test
  public void purge_disables_invocation() {
    triggerPurge();
    try {
      mock.getObject();
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void purge_disables_stubbing() {
    triggerPurge();
    try {
      given(willReturn(object), mock);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void purge_disables_verification() {
    triggerPurge();
    try {
      thenCalled(mock);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void purge_requires_double_when() {
    given(willReturn(object), onInstance(mock));
    when("");
    assertSame(object, mock.getObject());
    when("");
    try {
      mock.getObject();
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void purges_previous_when() {
    when(mock.getObject());
    triggerPurge();
    thenCalledTimes(0, onInstance(mock));
  }

  @Test
  public void purges_previous_when_closure() {
    when(new Closure() {
      public Object invoke() {
        mock.getObject();
        return null;
      }
    });
    triggerPurge();
    thenCalledTimes(0, onInstance(mock));
  }

  @Test
  public void purges_previous_when_chained() {
    when(mock).getObject();
    triggerPurge();
    thenCalledTimes(0, onInstance(mock));
  }

  private static void triggerPurge() {
    when("");
    when("");
  }

  @Test
  public void purge_now_disables_invocation() {
    triggerPurgeNow();
    try {
      mock.getObject();
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void purge_now_disables_stubbing() {
    triggerPurgeNow();
    try {
      given(willReturn(object), mock);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void purge_now_disables_verification() {
    triggerPurgeNow();
    try {
      thenCalled(mock);
      fail();
    } catch (TestoryException e) {}
  }

  private static void triggerPurgeNow() {
    givenTest(new Object());
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
