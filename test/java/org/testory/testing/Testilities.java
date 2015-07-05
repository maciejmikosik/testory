package org.testory.testing;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Testilities {

  public static String printStackTrace(Throwable throwable) {
    StringWriter writer = new StringWriter();
    throwable.printStackTrace(new PrintWriter(writer));
    return writer.toString();
  }

  public static StackTraceElement here() {
    return new Exception().getStackTrace()[1];
  }

  public static StackTraceElement nextLine(StackTraceElement element) {
    return new StackTraceElement(element.getClassName(), element.getMethodName(),
        element.getFileName(), element.getLineNumber() + 1);
  }
}
