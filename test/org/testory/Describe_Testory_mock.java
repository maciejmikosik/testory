package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.testory.Testory.mock;
import static org.testory.Testory.when;

import org.junit.Test;

public class Describe_Testory_mock {
  private Object mock;

  @Test
  public void should_mock_implement_to_string() {
    mock = mock(Object.class);
    assertTrue(mock.toString().contains("mock"));
    assertTrue(mock.toString().contains(mock.getClass().getName()));
  }

  @Test
  public void should_mock_implement_to_string_after_purging() {
    mock = mock(Object.class);
    triggerPurge();
    assertTrue(mock.toString().contains("mock"));
    assertTrue(mock.toString().contains(mock.getClass().getName()));
  }

  @Test
  public void should_mock_be_equal_to_itself() {
    mock = mock(Object.class);
    assertEquals(mock, mock);
  }

  @Test
  public void should_mock_be_equal_to_itself_after_purging() {
    mock = mock(Object.class);
    triggerPurge();
    assertEquals(mock, mock);
  }

  @Test
  public void should_mock_implement_hashcode() {
    mock = mock(Object.class);
    assertEquals(mock.hashCode(), mock.hashCode());
  }

  @Test
  public void should_mock_implement_hashcode_after_purging() {
    mock = mock(Object.class);
    triggerPurge();
    assertEquals(mock.hashCode(), mock.hashCode());
  }

  private void triggerPurge() {
    when(mock).toString();
    when(mock).toString();
    when(mock).toString();
  }
}
