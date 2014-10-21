package org.testory;

import static org.junit.Assert.fail;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.willReturn;

import org.junit.Test;

public class Describe_resetting {
  private Object mock;

  @Test
  public void reset_makes_mock_uncallable() {
    mock = mock(Object.class);
    givenTest(new Object());
    try {
      mock.toString();
      fail();
    } catch (TestoryException e) {}

  }

  @Test
  public void reset_makes_mock_unstubbable() {
    mock = mock(Object.class);
    givenTest(new Object());
    try {
      given(willReturn(null), mock);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void reset_makes_mock_unverifiable() {
    mock = mock(Object.class);
    givenTest(new Object());
    try {
      thenCalled(mock);
      fail();
    } catch (TestoryException e) {}
  }
}
