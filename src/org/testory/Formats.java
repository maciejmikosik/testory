package org.testory;

import static org.testory.common.Objects.print;

import org.testory.common.Nullable;

// TODO test Formats
public class Formats {
  public static String formatSection(String caption, @Nullable Object content) {
    return "" //
        + "  " + caption + "\n" //
        + "    " + print(content) + "\n";
  }
}
