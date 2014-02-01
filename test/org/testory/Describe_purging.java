package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.mock;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.junit.Before;
import org.junit.Test;

public class Describe_purging {
  private Object mock, value;
  private String string;

  @Before
  public void before() {
    string = "string";
  }

  @Test
  public void mock_prestubbings_survive_purge() {
    mock = mock(Object.class);
    String toString = mock.toString();
    boolean equals = mock.equals(mock);
    int hashcode = mock.hashCode();

    triggerPurge();
    assertEquals(toString, mock.toString());
    assertEquals(equals, mock.equals(mock));
    assertEquals(hashcode, mock.hashCode());
  }

  @Test
  public void injected_mock_prestubbings_survive_purge() {
    class TestClass {
      Object field;
    }
    TestClass test = new TestClass();
    givenTest(test);
    mock = test.field;
    String toString = mock.toString();
    boolean equals = mock.equals(mock);
    int hashcode = mock.hashCode();

    triggerPurge();

    assertEquals(toString, test.field.toString());
    assertEquals(equals, mock.equals(mock));
    assertEquals(hashcode, test.field.hashCode());
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
    value = mock.toString();
    given(willReturn(string), mock).toString();
    when("");
    when("");
    assertEquals(value, mock.toString());
  }

  private void triggerPurge() {
    when("");
    when("");
  }
}
