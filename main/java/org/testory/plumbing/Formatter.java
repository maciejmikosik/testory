package org.testory.plumbing;

import static org.testory.common.CharSequences.join;
import static org.testory.common.Objects.print;
import static org.testory.plumbing.PlumbingException.check;
import static org.testory.plumbing.Stubbing.stubbing;
import static org.testory.plumbing.history.FilteredHistory.filter;

import java.util.List;

import org.testory.common.Chain;
import org.testory.common.Nullable;
import org.testory.plumbing.history.FilteredHistory;
import org.testory.plumbing.history.History;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class Formatter {
  private int isFormatting = 0;

  private Formatter() {}

  public static Formatter formatter() {
    return new Formatter();
  }

  public String format(@Nullable Object object) {
    isFormatting++;
    try {
      return print(object);
    } finally {
      isFormatting--;
    }
  }

  public String formatJoin(CharSequence separator, List<?> iterable) {
    isFormatting++;
    try {
      return join(separator, iterable).toString();
    } finally {
      isFormatting--;
    }
  }

  public String format(Invocation invocation) {
    isFormatting++;
    try {
      return String.format("%s.%s(%s)",
          format(invocation.instance),
          invocation.method.getName(),
          formatJoin(",", invocation.arguments));
    } finally {
      isFormatting--;
    }
  }

  private boolean isFormatting() {
    return isFormatting > 0;
  }

  public History plug(final History history) {
    check(history != null);
    final Object stubbingToString = stubbingToString(history);
    return new History() {
      public void set(Chain<Object> events) {
        if (isFormatting()) {
          throw new UnsupportedOperationException();
        }
        history.set(events);
      }

      public Chain<Object> get() {
        return isFormatting()
            ? history.get().add(stubbingToString)
            : history.get();
      }

      public History add(Object event) {
        if (!isFormatting()) {
          history.add(event);
        }
        return this;
      }
    };
  }

  private static Object stubbingToString(History history) {
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
