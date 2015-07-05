package org.testory.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.testory.testing.Testilities.newThrowable;

import org.junit.Before;
import org.junit.Test;

public class describe_Testilities_newThrowable {
  private String name;
  private Throwable throwable;

  @Before
  public void before() {
    name = "name";
  }

  @Test
  public void should_return_not_null() {
    throwable = newThrowable(name);
    assertNotNull(throwable);
  }

  @Test
  public void should_print_name() {
    throwable = newThrowable(name);
    assertEquals(name, throwable.toString());
  }

  @Test
  public void should_fail_for_null_name() {
    try {
      newThrowable(null);
      fail();
    } catch (NullPointerException e) {}
  }
}
