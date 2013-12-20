package org.testory;

import static org.testory.common.Objects.print;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.testory.common.Nullable;

// TODO test Formats
public class Formats {
  public static String formatSection(String caption, @Nullable Object content) {
    return "" //
        + "  " + caption + "\n" //
        + "    " + print(content) + "\n";
  }

  public static String formatThrowable(Throwable throwable) {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    throwable.printStackTrace(printWriter);
    printWriter.write("\n");
    printWriter.close();
    return stringWriter.toString();
  }
}
