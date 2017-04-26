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

  public void cannotBeNull(Object object) {
    if (object == null) {
      fail("cannot be null");
    }
  }

  public void mustBeMatcher(Object matcher) {
    cannotBeNull(matcher);
    if (!Matchers.isMatcher(matcher)) {
      fail("cannot be matcher");
    }
  }

  public void mustBeMock(Object mock) {
    cannotBeNull(mock);
    for (Mocking mocking : mockingHistory.get()) {
      if (mocking.mock == mock) {
        return;
      }
    }
    fail("must be mock");
  }

  public void cannotBeNegative(int number) {
    if (number < 0) {
      fail("cannot be negative");
    }
  }

  public void mustCallWhen() {
    if (inspectingHistory.get().size() == 0) {
      fail("must call when");
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
