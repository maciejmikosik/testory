package org.objenesis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

import java.util.AbstractList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class Learn_Objenesis {
  private Objenesis objenesis;
  private boolean invoked;

  @Before
  public void before() {
    objenesis = new ObjenesisStd();
  }

  @Test
  public void creates_unique_object() {
    assertNotSame(objenesis.newInstance(Object.class), objenesis.newInstance(Object.class));
  }

  @Test
  public void creates_unique_string() {
    assertNotSame(objenesis.newInstance(String.class), objenesis.newInstance(String.class));
  }

  @Test
  public void creates_unique_boolean_wrapper() {
    assertEquals(Boolean.FALSE, objenesis.newInstance(Boolean.class));
    assertNotSame(Boolean.FALSE, objenesis.newInstance(Boolean.class));
    assertNotSame(objenesis.newInstance(Boolean.class), objenesis.newInstance(Boolean.class));
  }

  private static enum Singleton {
    INSTANCE;
  }

  @Test
  public void creates_unique_enum() {
    assertNotSame(Singleton.INSTANCE, objenesis.newInstance(Singleton.class));
    assertNotSame(objenesis.newInstance(Singleton.class), objenesis.newInstance(Singleton.class));
  }

  @Test
  public void does_not_invoke_constructor() {
    class Instance {
      @SuppressWarnings("unused")
      Instance() {
        invoked = true;
      }
    }
    objenesis.newInstance(Instance.class);
    assertFalse(invoked);
  }

  @Test
  public void cannot_create_interface() {
    try {
      objenesis.newInstance(List.class);
      fail();
    } catch (ObjenesisException e) {}
  }

  @Test
  public void cannot_create_abstract_class() {
    try {
      objenesis.newInstance(AbstractList.class);
      fail();
    } catch (ObjenesisException e) {}
  }

  @Test
  public void cannot_create_annotation() {
    try {
      objenesis.newInstance(SuppressWarnings.class);
      fail();
    } catch (ObjenesisException e) {}
  }

  @Test
  public void cannot_create_object_array() {
    try {
      objenesis.newInstance(Object[].class);
      fail();
    } catch (ObjenesisException e) {}
  }

  @Test
  public void cannot_create_primitive_array() {
    try {
      objenesis.newInstance(int[].class);
      fail();
    } catch (ObjenesisException e) {}
  }

  /**
   * crashes jvm
   */
  @Ignore
  @Test
  public void cannot_create_primitive() {
    try {
      objenesis.newInstance(int.class);
      fail();
    } catch (ObjenesisException e) {}
  }
}
