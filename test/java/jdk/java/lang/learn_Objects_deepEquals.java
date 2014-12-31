package jdk.java.lang;

import static org.junit.Assert.assertTrue;

import java.util.Objects;

import org.junit.Test;

public class learn_Objects_deepEquals {
  private Object[] array, otherArray;

  @Test
  public void check_element_type_at_runtime() {
    array = new Object[] { "a", new Object[] { "b" } };
    otherArray = new Object[] { "a", new Object[] { "b" } };
    assertTrue(Objects.deepEquals(array, otherArray));
  }
}
