package org.testory;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.testory.Testory.given;
import static org.testory.test.Testilities.newObject;
import static org.testory.test.Testilities.returning;

import org.junit.Before;
import org.junit.Test;

public class Describe_Testory_given {
  private Object object;

  @Before
  public void before() {
    object = newObject("object");
  }

  @Test
  public void should_return_same_object() {
    Object given = given(object);
    assertSame(given, object);
  }

  @Test
  public void should_return_null_object() {
    Object given = given((Object) null);
    assertSame(given, null);
  }

  @SuppressWarnings("deprecation")
  @Test
  public void should_fail_for_any_closure() {
    try {
      given(returning(null));
      fail();
    } catch (TestoryException e) {}
  }

  @SuppressWarnings("deprecation")
  @Test
  public void should_fail_for_null_closure() {
    try {
      given(null);
      fail();
    } catch (TestoryException e) {}
  }
}
