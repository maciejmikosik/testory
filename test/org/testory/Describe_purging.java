package org.testory;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.mock;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.junit.Before;
import org.junit.Test;

public class Describe_purging {
  private Object mock;
  private String string;

  @Before
  public void before() {
    string = "string";
  }

  @Test
  public void mock_is_unusable_after_purging() {
    mock = mock(Object.class);

    triggerPurge();

    try {
      mock.toString();
      fail();
    } catch (TestoryException e) {}
    try {
      mock.equals(mock);
      fail();
    } catch (TestoryException e) {}
    try {
      mock.hashCode();
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void injected_mock_is_unusable_after_purging() {
    class TestClass {
      Object field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    mock = test.field;

    triggerPurge();

    try {
      mock.toString();
      fail();
    } catch (TestoryException e) {}
    try {
      mock.equals(mock);
      fail();
    } catch (TestoryException e) {}
    try {
      mock.hashCode();
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void single_when_does_not_purge_stubbing() {
    mock = mock(Object.class);
    given(willReturn(string), mock).toString();
    when("");
    assertSame(string, mock.toString());
  }

  @Test
  public void double_when_purges_stubbings() {
    mock = mock(Object.class);
    given(willReturn(string), mock).toString();
    when("");
    when("");
    try {
      mock.toString();
      fail();
    } catch (TestoryException e) {}
  }

  private void triggerPurge() {
    when("");
    when("");
  }
}
