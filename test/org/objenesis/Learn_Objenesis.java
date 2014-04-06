package org.objenesis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

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
  public void creates_unique_empty_string() {
    assertEquals("", objenesis.newInstance(String.class));
    assertNotSame("", objenesis.newInstance(String.class));
    assertNotSame(objenesis.newInstance(String.class), objenesis.newInstance(String.class));
  }

  @Test
  public void creates_unique_boolean_wrapper() {
    assertEquals(Boolean.FALSE, objenesis.newInstance(Boolean.class));
    assertNotSame(Boolean.FALSE, objenesis.newInstance(Boolean.class));
    assertNotSame(objenesis.newInstance(Boolean.class), objenesis.newInstance(Boolean.class));
  }

  private static final class Singleton {
    public static final Singleton INSTANCE = new Singleton();

    private Singleton() {}
  }

  @Test
  public void creates_unique_singleton() {
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
