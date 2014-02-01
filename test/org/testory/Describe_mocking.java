package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.mock;
import static org.testory.test.Testilities.newObject;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class Describe_mocking {
  private Object mock, object;

  @Before
  public void before() {
    object = newObject("object");
  }

  @Test
  public void concrete_class_is_mockable() {
    mock = mock(ArrayList.class);
    assertTrue(mock instanceof ArrayList);
  }

  @Test
  public void abstract_class_is_mockable() {
    mock = mock(AbstractList.class);
    assertTrue(mock instanceof AbstractList);
  }

  @Test
  public void interface_is_mockable() {
    mock = mock(List.class);
    assertTrue(mock instanceof List);
  }

  @Test
  public void final_class_is_not_mockable() {
    final class FinalClass {}
    try {
      mock(FinalClass.class);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void to_string_is_prestubbed_with_class_name_and_identity_hashcode() {
    mock = mock(Object.class);
    assertTrue(mock.toString().contains("mock"));
    assertTrue(mock.toString().contains(mock.getClass().getName()));
    assertTrue(mock.toString().contains("" + System.identityHashCode(mock)));
  }

  @Test
  public void equals_is_prestubbed_so_mock_is_equal_only_to_itself() {
    mock = mock(Object.class);
    assertEquals(mock, mock);
    assertNotEquals(object, mock);
  }

  @Test
  public void hashcode_is_prestubbed_to_identity_hashcode() {
    mock = mock(Object.class);
    assertEquals(mock.hashCode(), System.identityHashCode(mock));
  }
}
