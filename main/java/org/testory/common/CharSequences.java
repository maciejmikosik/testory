package org.testory.common;

import static org.testory.common.Checks.checkNotNull;
import static org.testory.common.Objects.print;

import java.util.Iterator;
import java.util.List;

public class CharSequences {
  public static CharSequence join(CharSequence separator, List<?> elements) {
    checkNotNull(separator);
    checkNotNull(elements);
    StringBuilder builder = new StringBuilder();
    Iterator<?> iterator = elements.iterator();
    if (iterator.hasNext()) {
      builder.append(print(iterator.next()));
    }
    while (iterator.hasNext()) {
      builder.append(separator).append(print(iterator.next()));
    }
    return builder;
  }
}
