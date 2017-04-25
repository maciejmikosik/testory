package org.testory.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.common.Effect.returned;
import static org.testory.common.Effect.returnedVoid;
import static org.testory.common.Effect.thrown;
import static org.testory.testing.Fakes.newObject;
import static org.testory.testing.Fakes.newThrowable;

import org.junit.Before;
import org.junit.Test;
import org.testory.common.Effect.ReturnedObject;
import org.testory.common.Effect.ReturnedVoid;
import org.testory.common.Effect.Thrown;

public class TestEffect {
  private Object object;
  private Throwable throwable;
  private Effect effect;
  private ReturnedObject returned;
  private Thrown thrown;

  @Before
  public void before() {
    object = newObject("object");
    throwable = newThrowable("throwable");
  }

  @Test
  public void returned_object_holds_object() {
    returned = returned(object);
    assertSame(object, returned.object);
  }

  @Test
  public void returned_null_holds_null() {
    returned = returned(null);
    assertNull(returned.object);
  }

  @Test
  public void returned_void_is_different_class() {
    effect = returnedVoid();
    assertTrue(effect instanceof ReturnedVoid);
  }

  @Test
  public void thrown_throwable_holds_throwable() {
    thrown = thrown(throwable);
    assertSame(throwable, thrown.throwable);
  }

  @Test
  public void thrown_throwable_cannot_hold_null() {
    try {
      thrown(null);
      fail();
    } catch (NullPointerException e) {}
  }

  @Test
  public void prints_returned_object() {
    effect = returned(object);
    assertEquals("returned(" + object + ")", effect.toString());
  }

  @Test
  public void prints_returned_null() {
    effect = returned(null);
    assertEquals("returned(" + null + ")", effect.toString());
  }

  @Test
  public void prints_returned_void() {
    effect = returnedVoid();
    assertEquals("returnedVoid()", effect.toString());
  }

  @Test
  public void prints_thrown_throwable() {
    effect = thrown(throwable);
    assertEquals("thrown(" + throwable + ")", effect.toString());
  }
}
