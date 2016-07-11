package org.testory;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.testory.Testory.given;
import static org.testory.testing.Closures.returning;
import static org.testory.testing.Fakes.newObject;

import org.junit.Before;
import org.junit.Test;

public class test_given {
  private Object object;

  @Before
  public void before() {
    object = newObject("object");
  }

  @Test
  public void returns_same_object() {
    Object given = given(object);
    assertSame(object, given);
  }

  @Test
  public void returns_null_object() {
    Object given = given((Object) null);
    assertSame(null, given);
  }

  @Test
  public void returns_same_object_of_final_class() {
    final class FinalClass {}
    object = new FinalClass();
    Object given = given(object);
    assertSame(object, given);
  }

  @Test
  public void closure_is_forbidden() {
    try {
      given(returning(null));
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void null_closure_is_forbidden() {
    try {
      given(null);
      fail();
    } catch (TestoryException e) {}
  }
}
