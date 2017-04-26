package org.testory.plumbing;

import static org.testory.plumbing.history.FilteredHistory.filter;

import java.lang.reflect.Constructor;

import org.testory.common.Matchers;
import org.testory.plumbing.history.FilteredHistory;
import org.testory.plumbing.history.History;

public class Checker {
  private final FilteredHistory<Mocking> mockingHistory;
  private final FilteredHistory<Inspecting> inspectingHistory;
  private final Constructor<? extends RuntimeException> exceptionConstructor;

  private Checker(
      FilteredHistory<Mocking> mockingHistory,
      FilteredHistory<Inspecting> inspectingHistory,
      Constructor<? extends RuntimeException> exceptionConstructor) {
    this.mockingHistory = mockingHistory;
    this.inspectingHistory = inspectingHistory;
    this.exceptionConstructor = exceptionConstructor;
  }

  public static Checker checker(History history, Class<? extends RuntimeException> exceptionType) {
    try {
      return new Checker(
          filter(Mocking.class, history),
          filter(Inspecting.class, history),
          exceptionType.getConstructor(String.class));
    } catch (NoSuchMethodException e) {
      throw new PlumbingException(e);
    }
  }

  public void notNull(Object object) {
    if (object == null) {
      fail("expected not null");
    }
  }

  public void matcher(Object matcher) {
    notNull(matcher);
    if (!Matchers.isMatcher(matcher)) {
      fail("expected matcher");
    }
  }

  public void mock(Object mock) {
    notNull(mock);
    for (Mocking mocking : mockingHistory.get()) {
      if (mocking.mock == mock) {
        return;
      }
    }
    fail("expected mock");
  }

  public void notNegative(int number) {
    if (number < 0) {
      fail("expected not negative number");
    }
  }

  public void mustCallWhen() {
    if (inspectingHistory.get().size() == 0) {
      fail("expected call to when");
    }
  }

  private void fail(String string) {
    try {
      throw exceptionConstructor.newInstance(string);
    } catch (ReflectiveOperationException e) {
      throw new PlumbingException(e);
    }
  }
}
