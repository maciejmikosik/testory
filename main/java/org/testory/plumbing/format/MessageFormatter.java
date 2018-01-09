package org.testory.plumbing.format;

import static org.testory.common.SequenceFormatter.sequence;

import org.testory.common.Formatter;
import org.testory.common.Nullable;
import org.testory.common.ObjectFormatter;
import org.testory.proxy.Invocation;

public class MessageFormatter extends ObjectFormatter {
  protected MessageFormatter() {}

  public static Formatter messageFormatter() {
    return new MessageFormatter();
  }

  public String format(@Nullable Object object) {
    return object instanceof Invocation
        ? format((Invocation) object)
        : super.format(object);
  }

  private String format(Invocation invocation) {
    return String.format("%s.%s(%s)",
        format(invocation.instance),
        invocation.method.getName(),
        sequence(", ", this).format(invocation.arguments));
  }
}
