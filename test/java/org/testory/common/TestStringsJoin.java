package org.testory.common;

import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_LIST;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.common.Strings.join;

import org.junit.Before;
import org.junit.Test;

public class TestStringsJoin {
  private String x, a, b, c;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void joins_zero_elements() {
    when(join(x, EMPTY_LIST));
    thenReturned("");
  }

  @Test
  public void joins_zero_elements_varargs() {
    when(join(x));
    thenReturned("");
  }

  @Test
  public void joins_one_element() {
    when(join(x, asList(a)));
    thenReturned(a);
  }

  @Test
  public void joins_one_element_varargs() {
    when(join(x, a));
    thenReturned(a);
  }

  @Test
  public void joins_many_elements() {
    when(join(x, asList(a, b, c)));
    thenReturned(a + x + b + x + c);
  }

  @Test
  public void joins_many_elements_varargs() {
    when(join(x, a, b, c));
    thenReturned(a + x + b + x + c);
  }
}
