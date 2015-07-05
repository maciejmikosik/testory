package org.testory.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.common.Classes.setAccessible;

import java.lang.reflect.AccessibleObject;

import org.junit.Before;
import org.junit.Test;

public class test_Classes_setAccessible {
  private AccessibleObject accessible;

  @Before
  public void before() {
    accessible = new AccessibleObject() {};
    assertFalse(accessible.isAccessible());
  }

  @Test
  public void sets_object_accessible() {
    setAccessible(accessible);
    assertTrue(accessible.isAccessible());
  }

  @Test
  public void object_cannot_be_null() {
    try {
      setAccessible(null);
      fail();
    } catch (NullPointerException e) {}
  }
}
