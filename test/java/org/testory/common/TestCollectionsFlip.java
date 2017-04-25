package org.testory.common;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.common.Collections.flip;
import static org.testory.testing.Fakes.newObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TestCollectionsFlip {
  private List<Object> list, flipped, original;
  private Object a, b, c;

  @Before
  public void before() {
    a = newObject("a");
    b = newObject("b");
    c = newObject("c");
  }

  @Test
  public void flips_list() {
    list = asList(a, b, c);
    flipped = flip(list);
    assertEquals(asList(c, b, a), flipped);
  }

  @Test
  public void flips_empty_list() {
    list = asList();
    flipped = flip(list);
    assertEquals(asList(), flipped);
  }

  @Test
  public void does_not_change_original_list() {
    list = asList(a, b, c);
    original = new ArrayList<Object>(list);
    flip(list);
    assertEquals(original, list);
  }

  @Test
  public void infers_type_from_generic() {
    class Foo {}
    List<Foo> foos = Arrays.asList(new Foo());
    @SuppressWarnings("unused")
    List<Foo> inferred = flip(foos);
  }

  @Test
  public void fails_for_null_list() {
    try {
      flip(null);
      fail();
    } catch (NullPointerException e) {}
  }
}
