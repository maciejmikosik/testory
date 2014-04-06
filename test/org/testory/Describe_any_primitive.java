package org.testory;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.any;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.willReturn;

import org.junit.Before;
import org.junit.Test;

public class Describe_any_primitive {
  private Mock mock;

  @Before
  public void before() {
    mock = mock(Mock.class);
  }

  @Test
  public void fits_in_primitive() {
    given(willReturn(true), mock).primitive(any(Integer.class));
    assertTrue(mock.primitive(5));
  }

  @Test
  public void fits_in_wrapper() {
    given(willReturn(true), mock).wrapper(any(Integer.class));
    assertTrue(mock.wrapper(5));
  }

  @Test
  public void fits_in_wider_primitive() {
    given(willReturn(true), mock).primitive(any(Byte.class));
    assertTrue(mock.primitive(5));
  }

  @Test
  public void fails_for_primitive_type() {
    try {
      any(int.class);
      fail();
    } catch (TestoryException e) {}
  }

  public interface Mock {
    boolean wrapper(Integer o);

    boolean primitive(int o);
  }
}
