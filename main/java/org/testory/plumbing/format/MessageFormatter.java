package org.testory.plumbing.format;

import static org.testory.common.SequenceFormatter.sequence;
import static org.testory.plumbing.format.Body.body;

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
    if (object instanceof Invocation) {
      return format((Invocation) object);
    } else if (object instanceof Header) {
      return format((Header) object);
    } else if (object instanceof Body) {
      return format((Body) object);
    } else if (object instanceof Multiline) {
      return format((Multiline) object);
    } else {
      return super.format(object);
    }
  }

  private String format(Invocation invocation) {
    return String.format("%s.%s(%s)",
        format(invocation.instance),
        invocation.method.getName(),
        sequence(", ", this).format(invocation.arguments));
  }

  private String format(Header header) {
    return String.format("  %s\n", format(header.object));
  }

  private String format(Body body) {
    return String.format("    %s\n", format(body.object));
  }

  private String format(Multiline multiline) {
    StringBuilder builder = new StringBuilder();
    for (Object object : multiline.iterable) {
      builder.append(format(body(object)));
    }
    return builder.toString();
  }
}
