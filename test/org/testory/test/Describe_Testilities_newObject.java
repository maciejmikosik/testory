package org.testory.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.testory.test.Testilities.newObject;

import org.junit.Before;
import org.junit.Test;

public class Describe_Testilities_newObject {
  private String name;
  private Object object;

  @Before
  public void before() {
    name = "name";
  }

  @Test
  public void should_return_not_null() {
    object = newObject(name);
    assertNotNull(object);
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
