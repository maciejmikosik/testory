package org.testory;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;
import static org.testory.test.Testilities.newObject;

import org.junit.Before;
import org.junit.Test;
import org.testory.proxy.Invocation;

public class Describe_purging {
  private Mockable mock;
  private Object object;

  @Before
  public void before() {
    mock = mock(Mockable.class);
    object = newObject("object");
  }

  @Test
  public void disables_invocation() {
    triggerPurge();
    try {
      mock.getObject();
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void disables_stubbing() {
    triggerPurge();
    try {
      given(willReturn(object), mock);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void disables_verification() {
    triggerPurge();
    try {
      thenCalled(mock);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void requires_double_when() {
    given(willReturn(object), onInstance(mock));
    when("");
    assertSame(object, mock.getObject());
    when("");
    try {
      mock.getObject();
      fail();
    } catch (TestoryException e) {}
  }

  private static void triggerPurge() {
    when("");
    when("");
  }

  private static InvocationMatcher onInstance(final Object mock) {
    return new InvocationMatcher() {
      public boolean matches(Invocation invocation) {
        return invocation.instance == mock;
      }
    };
  }

  private static class Mockable {
    public Object getObject() {
      throw null;
    }
  }
}
