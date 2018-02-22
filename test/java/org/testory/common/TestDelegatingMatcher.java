package org.testory.common;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.testing.Fakes.newObject;

import org.junit.Before;
import org.junit.Test;

public class TestDelegatingMatcher {
  private Matcher matcher, decorated;
  private Object object, other;
  private String string;

  @Before
  public void before() {
    object = newObject("object");
    other = newObject("other");
    string = "string";
  }

  @Test
  public void delegates_matching() {
    matcher = new DelegatingMatcher(same(object));
    assertTrue(matcher.matches(object));
    assertFalse(matcher.matches(other));
  }

  @Test
  public void delegates_printing() {
    decorated = new Matcher() {
      public boolean matches(Object item) {
        return false;
      }

      public String toString() {
        return string;
      }
    };
    matcher = new DelegatingMatcher(decorated);
    assertEquals(decorated.toString(), matcher.toString());
  }

  @Test
  public void fails_for_null_matcher() {
    try {
      new DelegatingMatcher(null);
      fail();
    } catch (NullPointerException e) {}
  }

  private static Matcher same(final Object instance) {
    return new Matcher() {
      public boolean matches(Object item) {
        return instance == item;
      }

      public String toString() {
        return format("same(%s)", instance);
      }
    };
  }
}
