package org.testory;

import static org.junit.Assert.assertTrue;
import static org.testory.Testory.any;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.willReturn;

import org.junit.Before;
import org.junit.Test;

public class Describe_any_primitive {
  private Mockable mock;

  @Before
  public void before() {
    mock = mock(Mockable.class);
  }

  @Test
  public void fits_wrapper_in_primitive() {
    given(willReturn(true), mock).primitive(any(Integer.class));
    assertTrue(mock.primitive(5));
  }

  @Test
  public void fits_wrapper_in_wrapper() {
    given(willReturn(true), mock).wrapper(any(Integer.class));
    assertTrue(mock.wrapper(5));
  }

  @Test
  public void fits_wrapper_in_wider_primitive() {
    given(willReturn(true), mock).primitive(any(Byte.class));
    assertTrue(mock.primitive(5));
  }

  @Test
  public void fits_primitive_in_primitive() {
    given(willReturn(true), mock).primitive(any(int.class));
    assertTrue(mock.primitive(5));
  }

  @Test
  public void fits_primitive_in_wrapper() {
    given(willReturn(true), mock).wrapper(any(int.class));
    assertTrue(mock.wrapper(5));
  }

  @Test
  public void fits_primitive_in_wider_primitive() {
    given(willReturn(true), mock).primitive(any(byte.class));
    assertTrue(mock.primitive(5));
  }

  private static abstract class Mockable {
    abstract boolean wrapper(Integer o);

    abstract boolean primitive(int o);
  }
}
