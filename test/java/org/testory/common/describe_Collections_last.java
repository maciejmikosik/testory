package org.testory.common;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.testory.common.Collections.last;
import static org.testory.testing.Testilities.newObject;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class describe_Collections_last {
  private Object a, b, c;

  @Before
  public void before() {
    a = newObject("a");
    b = newObject("b");
    c = newObject("c");
  }

  @Test
  public void returns_last_element() {
    assertSame(c, last(Arrays.asList(a, b, c)));
  }

  @Test
  public void returns_sole_element() {
    assertSame(a, last(Arrays.asList(a)));
  }

  @Test
  public void fails_for_empty_list() {
    try {
      last(Arrays.asList());
      fail();
    } catch (RuntimeException e) {}
  }

  @Test
  public void fails_for_null_list() {
    try {
      last(null);
      fail();
    } catch (NullPointerException e) {}
  }

  @Test
  public void infers_type_from_generic() {
    class Foo {}
    List<Foo> list = Arrays.asList(new Foo());
    @SuppressWarnings("unused")
    Foo last = last(list);
  }

  @Test
  public void infers_type_from_generic_bounded_wildcard() {
    class Foo {}
    List<? extends Foo> list = Arrays.asList(new Foo());
    @SuppressWarnings("unused")
    Foo last = last(list);
  }
}
