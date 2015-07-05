package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.mock;
import static org.testory.Testory.when;
import static org.testory.testing.Fakes.newObject;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class test_mocking {
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
  public void mock_is_nice() {
    class Foo {
      Object getObject() {
        throw new RuntimeException();
      }

      int getInt() {
        throw new RuntimeException();
      }

      void getVoid() {
        throw new RuntimeException();
      }
    }
    Foo foo = mock(Foo.class);
    assertNull(foo.getObject());
    assertEquals(0, foo.getInt());
    foo.getVoid();
  }

  @Test
  public void to_string_is_prestubbed_with_class_name_and_ordinal_counting_from_last_purging() {
    when(0);
    mock = mock(ArrayList.class);
    assertEquals("mockArrayList0", mock.toString());
    mock = mock(ArrayList.class);
    assertEquals("mockArrayList1", mock.toString());
    when(0);
    mock = mock(ArrayList.class);
    assertEquals("mockArrayList0", mock.toString());
    mock = mock(ArrayList.class);
    assertEquals("mockArrayList1", mock.toString());
  }

  @Test
  public void to_string_is_prestubbed_with_inner_class_name() {
    class Inner {}
    mock = mock(Inner.class);
    assertEquals("mockInner0", mock.toString());
  }

  @Test
  public void to_string_of_object_is_prestubbed() {
    when(0);
    mock = mock(Object.class);
    assertEquals("mockObject0", mock.toString());
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
  public void hashcode_is_prestubbed_to_name_hash() {
    mock = mock(Object.class);
    assertEquals(mock.toString().hashCode(), mock.hashCode());
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

  @Test
  public void checks_if_not_null() {
    try {
      mock(null);
      fail();
    } catch (TestoryException e) {}
  }
}
