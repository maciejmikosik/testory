package org.testory.common;

import static java.util.Objects.requireNonNull;

public class SequenceFormatter {
  private final String separator;
  private final Formatter formatter;

  private SequenceFormatter(String separator, Formatter formatter) {
    this.separator = separator;
    this.formatter = formatter;
  }

  public static SequenceFormatter sequence(String separator, Formatter formatter) {
    return new SequenceFormatter(
        requireNonNull(separator),
        requireNonNull(formatter));
  }

  public String format(Iterable<?> elements) {
    requireNonNull(elements);
    StringBuilder builder = new StringBuilder();
    for (Object element : elements) {
      builder.append(formatter.format(element)).append(separator);
    }
    if (builder.length() > 0) {
      builder.delete(builder.length() - separator.length(), builder.length());
    }
    return builder.toString();
  }

  public String toString() {
    return String.format("sequence(%s, %s)", separator, formatter);
  }
}
