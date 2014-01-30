package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.when;
import static org.testory.test.Testilities.newObject;

import org.junit.Before;
import org.junit.Test;

public class Describe_Testory_thenCalled_Object {
  private Object object, otherObject;
  private Object mock, otherMock;

  @Before
  public void before() {
    object = newObject("object");
    otherObject = newObject("otherObject");
    mock = mock(Object.class);
    otherMock = mock(Object.class);
  }

  @Test
  public void should_verify_invocation() {
    mock.equals(object);
    thenCalled(mock).equals(object);
  }

  @Test
  public void should_verify_invocation_with_array_argument() {
    mock.equals(new Object[0]);
    thenCalled(mock).equals(new Object[0]);
  }

  @Test
  public void should_fail_for_invocation_on_different_instance() {
    otherMock.equals(object);
    try {
      thenCalled(mock).equals(object);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected called\n" //
          + "    " + mock + ".equals(" + object + ")" + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_for_invocation_of_different_method() {
    mock.toString();
    try {
      thenCalled(mock).equals(object);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected called\n" //
          + "    " + mock + ".equals(" + object + ")" + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_for_invocation_with_different_arguments() {
    mock.equals(otherObject);
    try {
      thenCalled(mock).equals(object);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected called\n" //
          + "    " + mock + ".equals(" + object + ")" + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_for_missing_invocation() {
    try {
      thenCalled(mock).equals(object);
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void should_fail_for_repeated_invocation() {
    mock.equals(object);
    mock.equals(object);
    try {
      thenCalled(mock).equals(object);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected called\n" //
          + "    " + mock + ".equals(" + object + ")" + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_verification_not_influence_following_verifications() {
    mock.equals(object);
    thenCalled(mock).equals(object);
    thenCalled(mock).equals(object);
  }

  @Test
  public void should_ignore_invocations_before_when() {
    mock.equals(object);
    when("do something");
    mock.equals(object);
    thenCalled(mock).equals(object);
  }

  @Test
  public void should_ignore_invocations_before_when_closure() {
    mock.equals(object);
    when(new Closure() {
      public Object invoke() throws Throwable {
        return null;
      }
    });
    mock.equals(object);
    thenCalled(mock).equals(object);
  }
}
