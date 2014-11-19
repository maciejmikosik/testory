package org.testory.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.common.Uniques.hasUniques;
import static org.testory.common.Uniques.unique;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class describe_Uniques {
  @Test
  public void supports_normal_classes() {
    assertSupports(List.class);
    assertSupports(ArrayList.class);
    assertSupports(AbstractList.class);
    assertSupports(Object.class);
    assertSupports(String.class);

  }

  @Test
  public void supports_arrays() {
    assertSupports(Object[].class);
    assertSupports(String[].class);
    assertSupports(int[].class);
    assertSupports(Object[][][].class);
    assertSupports(String[][][].class);
    assertSupports(int[][][].class);
  }

  @Test
  public void supports_wrappers() {
    assertSupports(Integer.class);
    assertSupports(Void.class);
  }

  @Test
  public void does_not_support_primitives() {
    assertNotSupports(int.class);
    assertNotSupports(void.class);
  }

  @Test
  public void null_cannot_be_type() {
    try {
      hasUniques(null);
      fail();
    } catch (NullPointerException e) {}
    try {
      unique(null);
      fail();
    } catch (NullPointerException e) {}
  }

  private static void assertSupports(Class<?> type) {
    String message = type.toString();
    assertTrue(message, hasUniques(type));
    assertTrue(message, type.isInstance(unique(type)));
    assertNotSame(message, unique(type), unique(type));
  }

  private static void assertNotSupports(Class<?> type) {
    String message = type.toString();
    assertFalse(message, hasUniques(type));
    try {
      unique(type);
      fail(message);
    } catch (IllegalArgumentException e) {}
  }
}
