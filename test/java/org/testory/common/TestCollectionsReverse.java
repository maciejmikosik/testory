package org.testory.common;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.common.Collections.reverse;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TestCollectionsReverse {
  private String a, b, c;
  private List<String> list;
  private List<String> reversed;

  @Before
  public void before() {
    a = "a";
    b = "b";
    c = "c";
  }

  @Test
  public void reverses_list() {
    list = asList(a, b, c);
    reversed = reverse(list);
    assertEquals(asList(c, b, a), reversed);
  }

  @Test
  public void reverses_singleton_list() {
    list = asList(a);
    reversed = reverse(list);
    assertEquals(asList(a), list);
  }

  @Test
  public void reverses_empty_list() {
    list = asList();
    reversed = reverse(list);
    assertEquals(asList(), list);
  }

  @Test
  public void does_not_change_original() {
    list = asList(a, b, c);
    reversed = reverse(list);
    assertEquals(asList(a, b, c), list);
  }

  @Test
  public void checks_that_list_is_not_null() {
    try {
      reverse(null);
      fail();
    } catch (NullPointerException e) {}
  }
}
