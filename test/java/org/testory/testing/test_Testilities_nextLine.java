package org.testory.testing;

import static org.junit.Assert.assertEquals;
import static org.testory.testing.Testilities.nextLine;

import org.junit.Before;
import org.junit.Test;

public class test_Testilities_nextLine {
  private String className;
  private String methodName;
  private String fileName;
  private int lineNumber;
  private StackTraceElement element, next;

  @Before
  public void before() {
    className = "className";
    methodName = "methodName";
    fileName = "fileName";
    lineNumber = 45;
    element = new StackTraceElement(className, methodName, fileName, lineNumber);
  }

  @Test
  public void copies_class_name() {
    next = nextLine(element);
    assertEquals(element.getClassName(), next.getClassName());
  }

  @Test
  public void copies_method_name() {
    next = nextLine(element);
    assertEquals(element.getMethodName(), next.getMethodName());
  }

  @Test
  public void copies_file_name() {
    next = nextLine(element);
    assertEquals(element.getFileName(), next.getFileName());
  }

  @Test
  public void increments_line_number() {
    next = nextLine(element);
    assertEquals(element.getLineNumber() + 1, next.getLineNumber());
  }
}
