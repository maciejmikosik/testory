package org.testory.plumbing.format;

import static org.testory.plumbing.PlumbingException.check;
import static org.testory.plumbing.history.FilteredHistory.filter;
import static org.testory.plumbing.mock.Stubbed.stubbed;

import org.testory.common.Chain;
import org.testory.common.Formatter;
import org.testory.common.Nullable;
import org.testory.plumbing.history.FilteredHistory;
import org.testory.plumbing.history.History;
import org.testory.plumbing.mock.Mocked;
import org.testory.plumbing.mock.Stubbed;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class QuietFormatter implements Formatter {
  private final Formatter formatter;

  private int isFormatting = 0;

  private QuietFormatter(Formatter formatter) {
    this.formatter = formatter;
  }

  public static QuietFormatter quiet(Formatter formatter) {
    check(formatter != null);
    return new QuietFormatter(formatter);
  }

  public String format(@Nullable Object object) {
    isFormatting++;
    try {
      return formatter.format(object);
    } finally {
      isFormatting--;
    }
  }

  public History quiet(final History history) {
    check(history != null);
    final Stubbed stubbedToString = stubbedToString(history);
    return new History() {
      public Chain<Object> get() {
        return isFormatting == 0
            ? history.get()
            : history.get().add(stubbedToString);
      }

      public void add(Object event) {
        if (isFormatting == 0) {
          history.add(event);
        }
      }

      public void cut(Chain<Object> tail) {
        if (isFormatting == 0) {
          history.cut(tail);
        }
      }
    };
  }

  private static Stubbed stubbedToString(History history) {
    final FilteredHistory<Mocked> mockedHistory = filter(Mocked.class, history);
    return stubbed(new InvocationMatcher() {
      public boolean matches(Invocation invocation) {
        return invocation.method.getName().equals("toString")
            && invocation.method.getParameterTypes().length == 0;
      }
    }, new Handler() {
      public Object handle(Invocation invocation) {
        for (Mocked mocked : mockedHistory.get()) {
          if (mocked.mock == invocation.instance) {
            return mocked.name;
          }
        }
        return "unknownMock";
      }
    });
  }
}
