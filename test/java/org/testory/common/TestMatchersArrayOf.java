package org.testory.common;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.common.Matchers.arrayOf;
import static org.testory.testing.Fakes.newObject;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TestMatchersArrayOf {
  private Matcher m, ma, mb, mc;
  private Matcher matcher;
  private Object a, b, c, x;
  private List<Matcher> elements;

  @Before
  public void before() {
    a = newObject("a");
    b = newObject("b");
    c = newObject("c");
    x = newObject("x");
    m = new Matcher() {
      public boolean matches(Object item) {
        return true;
      }
    };
    ma = same(a);
    mb = same(b);
    mc = same(c);
  }

  @Test
  public void requires_same_number_of_elements() {
    matcher = arrayOf(asList(m, m, m));
    assertFalse(matcher.matches(new Object[] {}));
    assertFalse(matcher.matches(new Object[] { a }));
    assertFalse(matcher.matches(new Object[] { a, a }));
    assertTrue(matcher.matches(new Object[] { a, a, a }));
    assertFalse(matcher.matches(new Object[] { a, a, a, a }));
    assertFalse(matcher.matches(new Object[] { a, a, a, a, a }));
  }

  @Test
  public void requires_all_elements_to_match() {
    matcher = arrayOf(asList(ma, mb, mc));
    assertTrue(matcher.matches(new Object[] { a, b, c }));
    assertFalse(matcher.matches(new Object[] { x, b, c }));
    assertFalse(matcher.matches(new Object[] { a, x, c }));
    assertFalse(matcher.matches(new Object[] { a, b, x }));
  }

  @Test
  public void requires_elements_in_order() {
    matcher = arrayOf(asList(ma, mb, mc));
    assertTrue(matcher.matches(new Object[] { a, b, c }));
    assertFalse(matcher.matches(new Object[] { a, c, b }));
    assertFalse(matcher.matches(new Object[] { b, a, c }));
    assertFalse(matcher.matches(new Object[] { b, c, a }));
    assertFalse(matcher.matches(new Object[] { c, a, b }));
    assertFalse(matcher.matches(new Object[] { c, b, a }));
  }

  @Test
  public void handles_primitive_arrays() {
    matcher = arrayOf(asList(m, m, m));
    assertTrue(matcher.matches(new int[] { 1, 2, 3 }));
  }

  @Test
  public void rejects_non_arrays() {
    matcher = arrayOf(asList(m));
    assertFalse(matcher.matches(new Object()));
    assertFalse(matcher.matches(null));
  }

  @Test
  public void prints_matchers() {
    matcher = arrayOf(asList(ma, mb, mc));
    assertEquals("arrayOf(" + ma + ", " + mb + ", " + mc + ")", matcher.toString());
  }

  @Test
  public void defensive_copy_matchers() {
    elements = new ArrayList<>(asList(m, m, m));
    matcher = arrayOf(elements);
    elements.clear();
    assertTrue(matcher.matches(new Object[] { a, a, a }));
    assertFalse(matcher.matches(new Object[] {}));
  }

  @Test
  public void fails_for_null_matcher() {
    try {
      arrayOf(asList(m, null, m));
      fail();
    } catch (NullPointerException e) {}
  }

  @Test
  public void fails_for_null_list() {
    try {
      arrayOf(null);
      fail();
    } catch (NullPointerException e) {}
  }

  private static Matcher same(final Object instance) {
    return new Matcher() {
      public boolean matches(Object item) {
        return instance == item;
      }

      public String toString() {
        return "same(" + instance + ")";
      }
    };
  }
}
