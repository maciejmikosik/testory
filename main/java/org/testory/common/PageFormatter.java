package org.testory.common;

import static java.util.Objects.requireNonNull;
import static org.testory.common.Chain.chain;

public class PageFormatter {
  private final Formatter formatter;
  private final Chain<Object> chain;

  private PageFormatter(Formatter formatter, Chain<Object> chain) {
    this.formatter = formatter;
    this.chain = chain;
  }

  public static PageFormatter pageFormatter(Formatter formatter) {
    return new PageFormatter(
        requireNonNull(formatter),
        chain());
  }

  public PageFormatter add(Object object) {
    return new PageFormatter(formatter, chain.add(object));
  }

  public String build() {
    StringBuilder builder = new StringBuilder();
    for (Object object : chain.reverse()) {
      builder.append(formatter.format(object));
    }
    return builder.toString();
  }
}
