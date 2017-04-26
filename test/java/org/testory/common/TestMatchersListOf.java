package org.testory.common;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.common.Matchers.listOf;
import static org.testory.testing.Fakes.newObject;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TestMatchersListOf {
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
    matcher = listOf(asList(m, m, m));
    assertFalse(matcher.matches(asList()));
    assertFalse(matcher.matches(asList(a)));
    assertFalse(matcher.matches(asList(a, a)));
    assertTrue(matcher.matches(asList(a, a, a)));
    assertFalse(matcher.matches(asList(a, a, a, a)));
    assertFalse(matcher.matches(asList(a, a, a, a, a)));
  }

  @Test
  public void requires_all_elements_to_match() {
    matcher = listOf(asList(ma, mb, mc));
    assertTrue(matcher.matches(asList(a, b, c)));
    assertFalse(matcher.matches(asList(x, b, c)));
    assertFalse(matcher.matches(asList(a, x, c)));
    assertFalse(matcher.matches(asList(a, b, x)));
  }

  @Test
  public void requires_elements_in_order() {
    matcher = listOf(asList(ma, mb, mc));
    assertTrue(matcher.matches(asList(a, b, c)));
    assertFalse(matcher.matches(asList(a, c, b)));
    assertFalse(matcher.matches(asList(b, a, c)));
    assertFalse(matcher.matches(asList(b, c, a)));
    assertFalse(matcher.matches(asList(c, a, b)));
    assertFalse(matcher.matches(asList(c, b, a)));
  }

  @Test
  public void rejects_non_arrays() {
    matcher = listOf(asList(m));
    assertFalse(matcher.matches(new Object()));
    assertFalse(matcher.matches(null));
  }

  @Test
  public void prints_matchers() {
    matcher = listOf(asList(ma, mb, mc));
    assertEquals("listOf(" + ma + ", " + mb + ", " + mc + ")", matcher.toString());
  }

  @Test
  public void defensive_copy_matchers() {
    elements = new ArrayList<>(asList(m, m, m));
    matcher = listOf(elements);
    elements.clear();
    assertTrue(matcher.matches(asList(a, a, a)));
    assertFalse(matcher.matches(asList()));
  }

  @Test
  public void fails_for_null_matcher() {
    try {
      listOf(asList(m, null, m));
      fail();
    } catch (NullPointerException e) {}
  }

  @Test
  public void fails_for_null_list() {
    try {
      listOf(null);
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
