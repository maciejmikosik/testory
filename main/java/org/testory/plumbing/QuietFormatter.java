package org.testory.plumbing;

import static java.lang.String.join;
import static org.testory.plumbing.PlumbingException.check;
import static org.testory.plumbing.Stubbing.stubbing;
import static org.testory.plumbing.history.FilteredHistory.filter;

import org.testory.common.Chain;
import org.testory.common.Formatter;
import org.testory.common.Nullable;
import org.testory.plumbing.history.FilteredHistory;
import org.testory.plumbing.history.History;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class QuietFormatter extends Formatter {
  private int isFormatting = 0;

  private QuietFormatter() {}

  public static QuietFormatter quietFormatter() {
    return new QuietFormatter();
  }

  public String format(@Nullable Object object) {
    isFormatting++;
    try {
      if (object instanceof Invocation) {
        return format((Invocation) object);
      } else {
        return super.format(object);
      }
    } finally {
      isFormatting--;
    }
  }

  private String format(Invocation invocation) {
    return String.format("%s.%s(%s)",
        format(invocation.instance),
        invocation.method.getName(),
        join(", ", formatSequence(invocation.arguments)));
  }

  public History quiet(final History history) {
    check(history != null);
    final Stubbing stubbingToString = stubbingToString(history);
    return new History() {
      public Chain<Object> get() {
        return isFormatting == 0
            ? history.get()
            : history.get().add(stubbingToString);
      }

      public History add(Object event) {
        if (isFormatting == 0) {
          history.add(event);
        }
        return this;
      }
    };
  }

  private static Stubbing stubbingToString(History history) {
    final FilteredHistory<Mocking> mockingHistory = filter(Mocking.class, history);
    return stubbing(new InvocationMatcher() {
      public boolean matches(Invocation invocation) {
        return invocation.method.getName().equals("toString")
            && invocation.method.getParameterTypes().length == 0;
      }
    }, new Handler() {
      public Object handle(Invocation invocation) {
        for (Mocking mocking : mockingHistory.get()) {
          if (mocking.mock == invocation.instance) {
            return mocking.name;
          }
        }
        return "unknownMock";
      }
    });
  }
}
