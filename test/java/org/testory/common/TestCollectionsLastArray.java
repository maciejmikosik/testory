package org.testory.common;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.testory.common.Collections.last;
import static org.testory.testing.Fakes.newObject;

import org.junit.Before;
import org.junit.Test;

public class TestCollectionsLastArray {
  private Object a, b, c;

  @Before
  public void before() {
    a = newObject("a");
    b = newObject("b");
    c = newObject("c");
  }

  @Test
  public void returns_last_element() {
    assertSame(c, last(new Object[] { a, b, c }));
  }

  @Test
  public void returns_sole_element() {
    assertSame(a, last(new Object[] { a }));
  }

  @Test
  public void fails_for_empty_array() {
    try {
      last(new Object[0]);
      fail();
    } catch (RuntimeException e) {}
  }

  @Test
  public void fails_for_null_array() {
    try {
      last((Object[]) null);
      fail();
    } catch (NullPointerException e) {}
  }

  @Test
  public void infers_type_from_generic() {
    class Foo {}
    Foo[] array = new Foo[] { new Foo() };
    @SuppressWarnings("unused")
    Foo last = last(array);
  }
}
