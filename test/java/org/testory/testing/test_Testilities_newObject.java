package org.testory.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.testing.Testilities.newObject;

import org.junit.Before;
import org.junit.Test;

public class test_Testilities_newObject {
  private String name, otherName;
  private Object object, otherObject;

  @Before
  public void before() {
    name = "name";
    otherName = "otherName";
  }

  @Test
  public void should_return_not_null() {
    object = newObject(name);
    assertNotNull(object);
  }

  @Test
  public void should_be_equal_to_itself() {
    object = newObject(name);
    assertTrue(object.equals(object));
  }

  @Test
  public void should_be_equal_to_object_with_same_name() {
    object = newObject(name);
    otherObject = newObject(name);
    assertTrue(object.equals(otherObject));
  }

  @Test
  public void should_not_be_equal_to_object_with_other_name() {
    object = newObject(name);
    otherObject = newObject(otherName);
    assertFalse(object.equals(otherObject));
  }

  @Test
  public void should_not_be_equal_to_other_class_with_same_to_string() {
    object = newObject(name);
    otherObject = new Object() {
      public String toString() {
        return name;
      }
    };
    assertFalse(object.equals(otherObject));
  }

  @Test
  public void should_not_be_equal_to_name() {
    object = newObject(name);
    assertFalse(object.equals(name));
  }

  @Test
  public void should_not_be_equal_to_null() {
    object = newObject(name);
    assertFalse(object.equals(null));
  }

  @Test
  public void should_implement_hashcode() {
    object = newObject(name);
    otherObject = newObject(name);
    assertEquals(object.hashCode(), otherObject.hashCode());
  }

  @Test
  public void should_print_name() {
    object = newObject(name);
    assertEquals(name, object.toString());
  }

  @Test
  public void should_fail_for_null_name() {
    try {
      newObject(null);
      fail();
    } catch (NullPointerException e) {}
  }
}
