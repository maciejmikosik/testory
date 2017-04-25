package org.testory.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

public class TestOptional {
  private Optional<Foo> optional;
  private Foo value;

  @Before
  public void before() {
    value = new Foo();
  }

  @Test
  public void empty_is_not_present() {
    optional = Optional.empty();
    assertFalse(optional.isPresent());
  }

  @Test
  public void cannot_get_from_empty() {
    optional = Optional.empty();
    try {
      optional.get();
      fail();
    } catch (NoSuchElementException e) {}
  }

  @Test
  public void optional_of_value_is_present() {
    optional = Optional.of(value);
    assertTrue(optional.isPresent());
  }

  @Test
  public void optional_of_value_gets_value() {
    optional = Optional.of(value);
    assertSame(value, optional.get());
  }

  @Test
  public void optional_value_cannot_be_null() {
    try {
      Optional.of(null);
      fail();
    } catch (NullPointerException e) {}
  }

  private static class Foo {}
}
