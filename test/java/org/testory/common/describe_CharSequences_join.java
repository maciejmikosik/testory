package org.testory.common;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.testory.common.CharSequences.join;
import static org.testory.test.Testilities.newObject;

import org.junit.Before;
import org.junit.Test;

public class describe_CharSequences_join {
  private Object a, b, c;
  private CharSequence separator;
  private CharSequence joined;

  @Before
  public void before() {
    separator = ":";
    a = newObject("a");
    b = newObject("b");
    c = newObject("c");
  }

  @Test
  public void joins_elements() {
    joined = join(separator, asList(a, b, c));
    assertEquals("" + a + separator + b + separator + c, joined.toString());
  }

  @Test
  public void joins_one_element() {
    joined = join(separator, asList(a));
    assertEquals("" + a, joined.toString());
  }

  @Test
  public void joins_no_elements() {
    joined = join(separator, asList());
    assertEquals("", joined.toString());
  }

  @Test
  public void joins_null_elements() {
    joined = join(separator, asList(null, null));
    assertEquals("" + null + separator + null, joined.toString());
  }
}
