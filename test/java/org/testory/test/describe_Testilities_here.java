package org.testory.test;

import static org.junit.Assert.assertEquals;
import static org.testory.test.Testilities.here;

import org.junit.Test;

public class describe_Testilities_here {
  private StackTraceElement element, otherElement;

  @Test
  public void stores_class_name() {
    element = here();
    assertEquals(describe_Testilities_here.class.getName(), element.getClassName());
  }

  @Test
  public void stores_method_name() {
    element = here();
    assertEquals("stores_method_name", element.getMethodName());
  }

  @Test
  public void stores_file_name() {
    element = here();
    assertEquals(describe_Testilities_here.class.getSimpleName() + ".java", element.getFileName());
  }

  @Test
  public void stores_line_number() {
    element = here();
    otherElement = here();
    assertEquals(element.getLineNumber() + 1, otherElement.getLineNumber());
  }
}
