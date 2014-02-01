package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.when;
import static org.testory.test.Testilities.newObject;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class Describe_verification {
  private Object object, otherObject;
  private List<Object> mock, otherMock;

  @Before
  public void before() {
    object = newObject("object");
    otherObject = newObject("otherObject");
    mock = mock(List.class);
    otherMock = mock(List.class);
  }

  @Test
  public void asserts_invocation_without_arguments() {
    mock.size();
    thenCalled(mock).size();
  }

  @Test
  public void asserts_invocation_with_argument() {
    mock.add(object);
    thenCalled(mock).add(object);
  }

  @Test
  public void fails_for_different_instance() {
    otherMock.size();
    try {
      thenCalled(mock).size();
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected called\n" //
          + "    " + mock + ".size()\n" //
      , e.getMessage());
    }
  }

  @Test
  public void fails_for_different_method() {
    mock.clear();
    try {
      thenCalled(mock).size();
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected called\n" //
          + "    " + mock + ".size()\n" //
      , e.getMessage());
    }
  }

  @Test
  public void fails_for_different_argument() {
    mock.add(otherObject);
    try {
      thenCalled(mock).add(object);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected called\n" //
          + "    " + mock + ".add(" + object + ")" + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void fails_for_missing_invocation() {
    try {
      thenCalled(mock).size();
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void fails_for_repeated_invocation() {
    mock.size();
    mock.size();
    try {
      thenCalled(mock).size();
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected called\n" //
          + "    " + mock + ".size()" + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void verification_does_not_affect_following_verifications() {
    mock.size();
    thenCalled(mock).size();
    thenCalled(mock).size();
  }

  @Test
  public void invocations_before_when_are_included() {
    mock.size();
    when("do something");
    thenCalled(mock).size();
  }

  @Test
  public void invocations_before_when_closure_are_included() {
    mock.size();
    when(new Closure() {
      public Object invoke() throws Throwable {
        return null;
      }
    });
    thenCalled(mock).size();
  }
}
