package org.testory.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.common.PageFormatter.pageFormatter;
import static org.testory.testing.Fakes.newObject;

import org.junit.Before;
import org.junit.Test;

public class TestPageFormatter {
  private Formatter formatter;
  private Object a, b, c;
  private String fa, fb, fc;
  private String page;

  @Before
  public void before() {
    a = newObject("a");
    b = newObject("b");
    c = newObject("c");
    fa = "fa";
    fb = "fb";
    fc = "fc";
    formatter = new Formatter() {
      public String format(Object object) {
        if (a == object) {
          return fa;
        } else if (b == object) {
          return fb;
        } else if (c == object) {
          return fc;
        } else {
          throw new RuntimeException();
        }
      }
    };
  }

  @Test
  public void builds_empty_page() {
    page = pageFormatter(formatter)
        .build();
    assertEquals("", page);
  }

  @Test
  public void builds_page_with_single_element() {
    page = pageFormatter(formatter)
        .add(a)
        .build();
    assertEquals("fa", page);
  }

  @Test
  public void builds_page_with_many_elements() {
    page = pageFormatter(formatter)
        .add(a)
        .add(b)
        .add(c)
        .build();
    assertEquals("fafbfc", page);
  }

  @Test
  public void formatter_cannot_be_null() {
    try {
      pageFormatter(null);
      fail();
    } catch (NullPointerException e) {}
  }
}
