package org.testory.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.common.Any.a;
import static org.testory.common.Any.any;
import static org.testory.common.Any.the;
import static org.testory.testing.Testilities.newObject;

import org.junit.Before;
import org.junit.Test;

public class describe_Any {
  private Matcher matcher;
  private Class<?> type;
  private Object object, otherObject;
  private Any any;
  private Foo foo;

  @Before
  public void before() {
    type = Foo.class;
    matcher = new Matcher() {
      public boolean matches(Object item) {
        return false;
      }
    };
    object = newObject("object");
    otherObject = newObject("otherObject");
    foo = new Foo() {};
  }

  @Test
  public void creates_token() {
    any = any(type, matcher);
    assertTrue(type.isInstance(any.token));
  }

  @Test
  public void stores_matcher() {
    any = any(type, matcher);
    assertSame(matcher, any.matcher);
  }

  @Test
  public void implements_to_string() {
    any = any(type, matcher);
    assertEquals("any(" + type.getName() + ", " + matcher.toString() + ")", any.toString());
  }

  @Test
  public void default_matcher_always_returns_true() {
    any = any(type);
    assertTrue(any.matcher.matches(object));
    assertTrue(any.matcher.matches(null));
    assertTrue(any.matcher.matches("string"));
  }

  @Test
  public void type_cannot_be_null() {
    try {
      any(null, matcher);
      fail();
    } catch (NullPointerException e) {}
    try {
      any(null);
      fail();
    } catch (NullPointerException e) {}
  }

  @Test
  public void matcher_cannot_be_null() {
    try {
      any(type, null);
      fail();
    } catch (NullPointerException e) {}
  }

  @Test
  public void a_creates_token_of_object_type() {
    any = a(foo);
    assertTrue(foo.getClass().isInstance(any.token));
  }

  @Test
  public void a_uses_equal_matcher() {
    any = a(object);
    assertTrue(any.matcher.matches(object));
    assertTrue(any.matcher.matches(newObject(object.toString())));
    assertFalse(any.matcher.matches(otherObject));
    assertFalse(any.matcher.matches(new Object()));
    assertFalse(any.matcher.matches(object.toString()));
    assertFalse(any.matcher.matches(null));
  }

  @Test
  public void a_implements_to_string() {
    any = a(object);
    assertEquals("a(" + object.toString() + ")", any.toString());
  }

  @Test
  public void the_creates_token_of_object_type() {
    any = the(foo);
    assertTrue(foo.getClass().isInstance(any.token));
  }

  @Test
  public void the_uses_same_matcher() {
    any = the(object);
    assertTrue(any.matcher.matches(object));
    assertFalse(any.matcher.matches(newObject(object.toString())));
    assertFalse(any.matcher.matches(otherObject));
    assertFalse(any.matcher.matches(new Object()));
    assertFalse(any.matcher.matches(object.toString()));
    assertFalse(any.matcher.matches(null));
  }

  @Test
  public void the_implements_to_string() {
    any = the(object);
    assertEquals("the(" + object.toString() + ")", any.toString());
  }

  private static class Foo {}
}
