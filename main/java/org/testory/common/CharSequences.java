package org.testory.common;

import static org.testory.common.Checks.checkNotNull;

import java.util.Iterator;
import java.util.List;

public class CharSequences {
  public static CharSequence join(CharSequence separator, List<?> elements) {
    checkNotNull(separator);
    checkNotNull(elements);
    StringBuilder builder = new StringBuilder();
    Iterator<?> iterator = elements.iterator();
    if (iterator.hasNext()) {
      builder.append(iterator.next());
    }
    while (iterator.hasNext()) {
      builder.append(separator).append(iterator.next());
    }
    return builder;
  }
}
