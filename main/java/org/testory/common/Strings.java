package org.testory.common;

import static java.util.Arrays.asList;

import java.util.Iterator;

public class Strings {
  // TODO replace by String.join in java 8
  public static String join(CharSequence delimiter, Iterable<? extends CharSequence> elements) {
    Iterator<? extends CharSequence> iterator = elements.iterator();
    if (!iterator.hasNext()) {
      return "";
    }
    StringBuilder builder = new StringBuilder();
    builder.append(iterator.next());
    while (iterator.hasNext()) {
      builder
          .append(delimiter)
          .append(iterator.next());
    }
    return builder.toString();
  }

  // TODO replace by String.join in java 8
  public static String join(CharSequence delimiter, CharSequence... elements) {
    return join(delimiter, asList(elements));
  }
}
