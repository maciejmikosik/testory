package org.testory;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenCalledTimes;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;
import static org.testory.testing.Fakes.newObject;
import static org.testory.testing.Purging.triggerPurge;

import org.junit.Before;
import org.junit.Test;
import org.testory.common.Closure;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class test_purging {
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
  public void does_not_purge_single_when() {
    given(willReturn(object), onInstance(mock));
    when("");
    assertSame(object, mock.getObject());
  }

  @Test
  public void does_not_purge_single_chained_when() {
    given(willReturn(object), onInstance(mock));
    when(new Object()).toString();
    assertSame(object, mock.getObject());
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
