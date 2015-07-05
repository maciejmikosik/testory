package org.testory.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.common.Collections.immutable;
import static org.testory.testing.Testilities.newObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class describe_Collections_immutable {
  private Object a, b, c;
  private Collection<Object> elements, immutable;
  private List<Object> list;
  private Set<Object> set;

  @Before
  public void before() {
    a = newObject("a");
    b = newObject("b");
    c = newObject("c");
    elements = Arrays.asList(a, b, c);
  }

  @Test
  public void list_is_equal() {
    immutable = immutable(list(elements));
    assertEquals(list(elements), immutable);
  }

  @Test
  public void list_is_unmodifiable() {
    immutable = immutable(list(elements));
    try {
      immutable.clear();
      fail();
    } catch (UnsupportedOperationException e) {}
  }

  @Test
  public void list_is_defensive_copied() {
    list = list(elements);
    immutable = immutable(list);
    list.clear();
    assertEquals(list(elements), immutable);
  }

  @Test
  public void set_is_equal() {
    immutable = immutable(set(elements));
    assertEquals(set(elements), immutable);
  }

  @Test
  public void set_is_unmodifiable() {
    immutable = immutable(set(elements));
    try {
      immutable.clear();
      fail();
    } catch (UnsupportedOperationException e) {}
  }

  @Test
  public void set_is_defensive_copied() {
    set = set(elements);
    immutable = immutable(set);
    set.clear();
    assertEquals(set(elements), immutable);
  }

  private static <E> List<E> list(Collection<E> elements) {
    return new ArrayList<E>(elements);
  }

  private static <E> Set<E> set(Collection<E> elements) {
    return new HashSet<E>(elements);
  }
}
