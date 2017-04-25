package org.testory.common;

import static org.junit.Assert.assertEquals;
import static org.testory.common.Objects.print;
import static org.testory.testing.Fakes.newObject;

import org.junit.Before;
import org.junit.Test;

public class TestObjectsPrint {
  private Object a, b, c, d;
  private String printed;

  @Before
  public void before() {
    a = newObject("a");
    b = newObject("b");
    c = newObject("c");
    d = newObject("d");
  }

  @Test
  public void prints_object_using_to_string_method() {
    printed = print(a);
    assertEquals(a.toString(), printed);
  }

  @Test
  public void prints_empty_array() {
    printed = print(new Object[] {});
    assertEquals("[]", printed);
  }

  @Test
  public void prints_single_object_array() {
    printed = print(new Object[] { a });
    assertEquals("[" + a + "]", printed);
  }

  @Test
  public void prints_primitive() {
    printed = print(new int[] { 1 });
    assertEquals("[" + 1 + "]", printed);
  }

  @Test
  public void prints_object_array() {
    printed = print(new Object[] { a, b });
    assertEquals("[" + a + ", " + b + "]", printed);
  }

  @Test
  public void prints_deep_array() {
    printed = print(new Object[] { new Object[] { a, b }, new Object[] { c, d } });
    assertEquals("[[" + a + ", " + b + "], [" + c + ", " + d + "]]", printed);
  }

  @Test
  public void prints_primitive_array() {
    printed = print(new int[][] { { 1, 2 }, { 3, 4 } });
    assertEquals("[[" + 1 + ", " + 2 + "], [" + 3 + ", " + 4 + "]]", printed);
  }

  @Test
  public void prints_null() {
    printed = print(null);
    assertEquals("null", printed);
  }
}
