package org.testory.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.common.Effect.getReturned;
import static org.testory.common.Effect.getThrown;
import static org.testory.common.Effect.hasReturned;
import static org.testory.common.Effect.hasReturnedObject;
import static org.testory.common.Effect.hasReturnedVoid;
import static org.testory.common.Effect.hasThrown;
import static org.testory.common.Effect.returned;
import static org.testory.common.Effect.returnedVoid;
import static org.testory.common.Effect.thrown;
import static org.testory.test.Testilities.newObject;
import static org.testory.test.Testilities.newThrowable;

import org.junit.Before;
import org.junit.Test;

public class describe_Effect {
  private Object object;
  private Throwable throwable;
  private Effect effect;

  @Before
  public void before() {
    object = newObject("object");
    throwable = newThrowable("throwable");
  }

  @Test
  public void should_create_returned_object() {
    effect = returned(object);
    assertTrue(hasReturned(effect));
    assertTrue(hasReturnedObject(effect));
    assertFalse(hasReturnedVoid(effect));
    assertFalse(hasThrown(effect));
  }

  @Test
  public void should_create_returned_null_object() {
    effect = returned(null);
    assertTrue(hasReturned(effect));
    assertTrue(hasReturnedObject(effect));
    assertFalse(hasReturnedVoid(effect));
    assertFalse(hasThrown(effect));
  }

  @Test
  public void should_create_returned_void() {
    effect = returnedVoid();
    assertTrue(hasReturned(effect));
    assertTrue(hasReturnedVoid(effect));
    assertFalse(hasReturnedObject(effect));
    assertFalse(hasThrown(effect));
  }

  @Test
  public void should_create_thrown() {
    effect = thrown(throwable);
    assertTrue(hasThrown(effect));
    assertFalse(hasReturned(effect));
    assertFalse(hasReturnedObject(effect));
    assertFalse(hasReturnedVoid(effect));
  }

  @Test
  public void should_get_returned_object() {
    effect = returned(object);
    assertSame(object, getReturned(effect));
  }

  @Test
  public void should_get_returned_null() {
    effect = returned(null);
    assertSame(null, getReturned(effect));
  }

  @Test
  public void should_not_get_returned_object_if_returned_void() {
    effect = returnedVoid();
    try {
      getReturned(effect);
      fail();
    } catch (IllegalArgumentException e) {}
  }

  @Test
  public void should_not_get_returned_object_if_thrown() {
    effect = thrown(throwable);
    try {
      getReturned(effect);
      fail();
    } catch (IllegalArgumentException e) {}
  }

  @Test
  public void should_get_thrown_throwable() {
    effect = thrown(throwable);
    assertSame(throwable, getThrown(effect));
  }

  @Test
  public void should_not_get_thrown_throwable_if_returned_object() {
    effect = returned(object);
    try {
      getThrown(effect);
      fail();
    } catch (IllegalArgumentException e) {}
  }

  @Test
  public void should_not_get_thrown_throwable_if_returned_void() {
    effect = returnedVoid();
    try {
      getThrown(effect);
      fail();
    } catch (IllegalArgumentException e) {}
  }

  @Test
  public void should_fail_for_null_throwable() {
    try {
      thrown(null);
      fail();
    } catch (NullPointerException e) {}
  }
}
