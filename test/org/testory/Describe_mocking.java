package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
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
  private String expected;

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
    mock = mock(ArrayList.class);
    expected = "mock_" + System.identityHashCode(mock) + "_" + ArrayList.class.getName();
    assertEquals(expected, mock.toString());

    mock = mock(List.class);
    expected = "mock_" + System.identityHashCode(mock) + "_" + List.class.getName();
    assertEquals(expected, mock.toString());
  }

  @Test
  public void overloaded_to_string_is_not_prestubbed() {
    class Foo {
      String toString(Foo foo) {
        return "notStubbed";
      }
    }
    Foo foo = mock(Foo.class);
    assertNull(foo.toString(new Foo()));
  }

  @Test
  public void equals_is_prestubbed_so_mock_is_equal_only_to_itself() {
    mock = mock(Object.class);
    assertEquals(mock, mock);
    assertNotEquals(object, mock);
  }

  @Test
  public void overloaded_equals_is_not_prestubbed() {
    class Foo {
      Object equals(Foo foo) {
        return new Object();
      }

      Object equals() {
        return new Object();
      }
    }
    Foo foo = mock(Foo.class);
    assertNull(foo.equals(new Foo()));
    assertNull(foo.equals());
  }

  @Test
  public void hashcode_is_prestubbed_to_identity_hashcode() {
    mock = mock(Object.class);
    assertEquals(mock.hashCode(), System.identityHashCode(mock));
  }

  @Test
  public void overloaded_hashcode_is_not_prestubbed() {
    class Foo {
      Object hashCode(Foo foo) {
        return new Object();
      }
    }
    Foo foo = mock(Foo.class);
    assertNull(foo.hashCode(new Foo()));
  }
}
