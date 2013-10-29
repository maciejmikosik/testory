package org.testory.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.common.Checks.checkNotNull;
import static org.testory.test.Testilities.newObject;

import org.junit.Before;
import org.junit.Test;

public class Describe_Checks_checkNotNull {
  private Object object;

  @Before
  public void before() {
    object = newObject("object");
  }

  @Test
  public void should_throw_null_pointer_exception_if_instance_is_null() {
    try {
      checkNotNull(null);
      fail();
    } catch (NullPointerException e) {}
  }

  @Test
  public void should_return_instance_if_not_null() {
    assertEquals(object, checkNotNull(object));
  }
}
