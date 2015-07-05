package org.testory.testing;

import static org.junit.Assert.assertEquals;
import static org.testory.testing.StackTraces.here;

import org.junit.Test;

public class test_StackTraces_here {
  private StackTraceElement element, otherElement;

  @Test
  public void stores_class_name() {
    element = here();
    assertEquals(test_StackTraces_here.class.getName(), element.getClassName());
  }

  @Test
  public void stores_method_name() {
    element = here();
    assertEquals("stores_method_name", element.getMethodName());
  }

  @Test
  public void stores_file_name() {
    element = here();
    assertEquals(test_StackTraces_here.class.getSimpleName() + ".java", element.getFileName());
  }

  @Test
  public void stores_line_number() {
    element = here();
    otherElement = here();
    assertEquals(element.getLineNumber() + 1, otherElement.getLineNumber());
  }
}
