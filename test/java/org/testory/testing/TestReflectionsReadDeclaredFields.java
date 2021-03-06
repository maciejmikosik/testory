package org.testory.testing;

import static org.junit.Assert.assertEquals;
import static org.testory.testing.Reflections.readDeclaredFields;

import java.util.Arrays;

import org.junit.Test;

public class TestReflectionsReadDeclaredFields {
  @SuppressWarnings("unused")
  @Test
  public void should_read_fields() {
    Object object = new Object() {
      String string = "string";
      Integer integer = 5;
      int intt = 6;
    };
    assertEquals(Arrays.asList("string", 5, 6), readDeclaredFields(object));
  }
}
