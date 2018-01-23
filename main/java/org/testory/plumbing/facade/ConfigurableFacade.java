package org.testory.plumbing.facade;

import static java.util.Objects.deepEquals;
import static org.testory.TestoryAssertionError.assertionError;
import static org.testory.common.Effect.returned;
import static org.testory.common.Effect.returnedVoid;
import static org.testory.common.Effect.thrown;
import static org.testory.common.Matchers.asMatcher;
import static org.testory.common.Matchers.isMatcher;
import static org.testory.common.Throwables.gently;
import static org.testory.common.Throwables.printStackTrace;
import static org.testory.plumbing.Inspecting.inspecting;
import static org.testory.plumbing.PlumbingException.check;
import static org.testory.plumbing.Stubbing.stubbing;
import static org.testory.plumbing.VerifyingInOrder.verifyInOrder;
import static org.testory.plumbing.history.FilteredHistory.filter;
import static org.testory.proxy.Invocation.invocation;
import static org.testory.proxy.Typing.subclassing;
import static org.testory.proxy.handler.DelegatingHandler.delegatingTo;
import static org.testory.proxy.handler.ReturningDefaultValueHandler.returningDefaultValue;
import static org.testory.proxy.handler.ReturningHandler.returning;
import static org.testory.proxy.handler.ThrowingHandler.throwing;

import org.testory.TestoryException;
import org.testory.common.Closure;
import org.testory.common.DiagnosticMatcher;
import org.testory.common.Effect;
import org.testory.common.Effect.Returned;
import org.testory.common.Effect.ReturnedObject;
import org.testory.common.Effect.Thrown;
import org.testory.common.Matcher;
import org.testory.common.Optional;
import org.testory.common.VoidClosure;
import org.testory.plumbing.Inspecting;
import org.testory.plumbing.VerifyingInOrder;
import org.testory.plumbing.history.FilteredHistory;
import org.testory.plumbing.im.wildcard.WildcardException;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class ConfigurableFacade implements Facade {
  private final Configuration configuration;
  private final FilteredHistory<Inspecting> inspectingHistory;
  private final FilteredHistory<Invocation> invocationHistory;

  private ConfigurableFacade(Configuration configuration) {
    this.configuration = configuration;
    inspectingHistory = filter(Inspecting.class, configuration.history);
    invocationHistory = filter(Invocation.class, configuration.history);
  }

  public static Facade configurableFacade(Configuration configuration) {
    check(configuration != null);
    return new ConfigurableFacade(configuration.validate());
  }

  public void givenTest(Object test) {
    try {
      configuration.injector.inject(test);
    } catch (RuntimeException e) {
      throw new TestoryException(e);
    }
  }

  public void given(Closure closure) {
    try {
      closure.invoke();
    } catch (Throwable e) {
      throw new TestoryException(e);
    }
  }

  public void given(VoidClosure closure) {
    given(asClosure(closure));
  }

  public <T> T given(T object) {
    return object;
  }

  public void given(boolean primitive) {}

  public void given(double primitive) {}

  public <T> T givenTry(T object) {
    Handler handler = new Handler() {
      public Object handle(Invocation invocation) {
        try {
          return invocation.invoke();
        } catch (Throwable e) {
          return null;
        }
      }
    };
    return proxyWrapping(object, handler);
  }

  public void givenTimes(int number, Closure closure) {
    for (int i = 0; i < number; i++) {
      try {
        closure.invoke();
      } catch (Throwable throwable) {
        throw gently(throwable);
      }
    }
  }

  public void givenTimes(int number, VoidClosure closure) {
    givenTimes(number, asClosure(closure));
  }

  public <T> T givenTimes(final int number, T object) {
    Handler handler = new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        for (int i = 0; i < number; i++) {
          invocation.invoke();
        }
        return null;
      }
    };
    return proxyWrapping(object, handler);
  }

  public <T> T mock(Class<T> type) {
    String name = configuration.mockNamer.name(type);
    return configuration.mockMaker.make(type, name);
  }

  public <T> T spy(T real) {
    Class<T> type = (Class<T>) real.getClass();
    T mock = mock(type);
    given(willSpy(real), onInstance(mock));
    return mock;
  }

  public <T> T given(final Handler handler, T mock) {
    return proxyWrapping(mock, new Handler() {
      public Object handle(Invocation invocation) {
        configuration.history.add(stubbing(matcherize(invocation), handler));
        return null;
      }
    });
  }

  public void given(Handler handler, InvocationMatcher invocationMatcher) {
    configuration.history.add(stubbing(invocationMatcher, handler));
  }

  public Handler willReturn(final Object object) {
    return returning(object);
  }

  public Handler willThrow(final Throwable throwable) {
    return new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        throw throwable.fillInStackTrace();
      }
    };
  }

  public Handler willRethrow(final Throwable throwable) {
    return throwing(throwable);
  }

  public Handler willSpy(final Object real) {
    return new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        return invocation(invocation.method, real, invocation.arguments).invoke();
      }
    };
  }

  public <T> T any(Class<T> type) {
    return (T) configuration.wildcardSupport.any(type);
  }

  public <T> T any(Class<T> type, Object matcher) {
    return (T) configuration.wildcardSupport.any(type, matcher);
  }

  public <T> T anyInstanceOf(Class<T> type) {
    return (T) configuration.wildcardSupport.anyInstanceOf(type);
  }

  public boolean a(boolean value) {
    return a((Boolean) value);
  }

  public char a(char value) {
    return a((Character) value);
  }

  public byte a(byte value) {
    return a((Byte) value);
  }

  public short a(short value) {
    return a((Short) value);
  }

  public int a(int value) {
    return a((Integer) value);
  }

  public long a(long value) {
    return a((Long) value);
  }

  public float a(float value) {
    return a((Float) value);
  }

  public double a(double value) {
    return a((Double) value);
  }

  public <T> T a(T value) {
    return (T) configuration.wildcardSupport.a(value);
  }

  public <T> T the(T value) {
    return (T) configuration.wildcardSupport.the(value);
  }

  public void the(boolean value) {
    throw new TestoryException();
  }

  public void the(double value) {
    throw new TestoryException();
  }

  public InvocationMatcher onInstance(final Object mock) {
    return new InvocationMatcher() {
      public boolean matches(Invocation invocation) {
        return invocation.instance == mock;
      }

      public String toString() {
        return "onInstance(" + mock + ")";
      }
    };
  }

  public InvocationMatcher onReturn(final Class<?> type) {
    return new InvocationMatcher() {
      public boolean matches(Invocation invocation) {
        return type == invocation.method.getReturnType();
      }

      public String toString() {
        return "onReturn(" + type.getName() + ")";
      }
    };
  }

  public InvocationMatcher onRequest(final Class<?> type, final Object... arguments) {
    return new InvocationMatcher() {
      public boolean matches(Invocation invocation) {
        return type == invocation.method.getReturnType()
            && deepEquals(arguments, invocation.arguments.toArray());
      }

      public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("onRequest(").append(type.getName());
        for (Object argument : arguments) {
          builder.append(", ").append(argument);
        }
        builder.append(")");
        return builder.toString();
      }
    };
  }

  public <T> T when(T object) {
    configuration.history.add(inspecting(returned(object)));
    try {
      return proxyWrapping(object, new Handler() {
        public Object handle(Invocation invocation) {
          configuration.history.add(inspecting(effectOf(invocation)));
          return null;
        }
      });
    } catch (RuntimeException e) {
      return null;
    }
  }

  private static Effect effectOf(Invocation invocation) {
    Object object;
    try {
      object = invocation.invoke();
    } catch (Throwable throwable) {
      return thrown(throwable);
    }
    return invocation.method.getReturnType() == void.class
        ? returnedVoid()
        : returned(object);
  }

  public void when(Closure closure) {
    configuration.history.add(inspecting(effectOf(closure)));
  }

  private static Effect effectOf(Closure closure) {
    Object object;
    try {
      object = closure.invoke();
    } catch (Throwable throwable) {
      return thrown(throwable);
    }
    return returned(object);
  }

  public void when(VoidClosure closure) {
    configuration.history.add(inspecting(effectOf(closure)));
  }

  private static Effect effectOf(VoidClosure closure) {
    try {
      closure.invoke();
    } catch (Throwable throwable) {
      return thrown(throwable);
    }
    return returnedVoid();
  }

  public void when(boolean value) {
    when((Object) value);
  }

  public void when(char value) {
    when((Object) value);
  }

  public void when(byte value) {
    when((Object) value);
  }

  public void when(short value) {
    when((Object) value);
  }

  public void when(int value) {
    when((Object) value);
  }

  public void when(long value) {
    when((Object) value);
  }

  public void when(float value) {
    when((Object) value);
  }

  public void when(double value) {
    when((Object) value);
  }

  public void thenReturned(Object objectOrMatcher) {
    Effect effect = getLastEffect();
    boolean expected = effect instanceof ReturnedObject
        && (deepEquals(objectOrMatcher, ((ReturnedObject) effect).object) || objectOrMatcher != null
            && isMatcher(objectOrMatcher)
            && asMatcher(objectOrMatcher).matches(((ReturnedObject) effect).object));
    if (!expected) {
      String diagnosis = objectOrMatcher != null && isMatcher(objectOrMatcher)
          && effect instanceof ReturnedObject
              ? tryFormatDiagnosis(objectOrMatcher, ((ReturnedObject) effect).object)
              : "";
      throw assertionError("\n"
          + formatSection("expected returned", objectOrMatcher)
          + formatBut(effect)
          + diagnosis);
    }
  }

  public void thenReturned(boolean value) {
    thenReturned((Object) value);
  }

  public void thenReturned(char value) {
    thenReturned((Object) value);
  }

  public void thenReturned(byte value) {
    thenReturned((Object) value);
  }

  public void thenReturned(short value) {
    thenReturned((Object) value);
  }

  public void thenReturned(int value) {
    thenReturned((Object) value);
  }

  public void thenReturned(long value) {
    thenReturned((Object) value);
  }

  public void thenReturned(float value) {
    thenReturned((Object) value);
  }

  public void thenReturned(double value) {
    thenReturned((Object) value);
  }

  public void thenReturned() {
    Effect effect = getLastEffect();
    boolean expected = effect instanceof Returned;
    if (!expected) {
      throw assertionError("\n"
          + formatSection("expected returned", "")
          + formatBut(effect));
    }
  }

  public void thenThrown(Object matcher) {
    Effect effect = getLastEffect();
    boolean expected = effect instanceof Thrown
        && asMatcher(matcher).matches(((Thrown) effect).throwable);
    if (!expected) {
      String diagnosis = effect instanceof Thrown
          ? tryFormatDiagnosis(matcher, ((Thrown) effect).throwable)
          : "";
      throw assertionError("\n"
          + formatSection("expected thrown", matcher)
          + formatBut(effect)
          + diagnosis);
    }
  }

  public void thenThrown(Throwable throwable) {
    Effect effect = getLastEffect();
    boolean expected = effect instanceof Thrown
        && deepEquals(throwable, ((Thrown) effect).throwable);
    if (!expected) {
      throw assertionError("\n"
          + formatSection("expected thrown", throwable)
          + formatBut(effect));
    }
  }

  public void thenThrown(Class<? extends Throwable> type) {
    Effect effect = getLastEffect();
    boolean expected = effect instanceof Thrown && type.isInstance(((Thrown) effect).throwable);
    if (!expected) {
      throw assertionError("\n"
          + formatSection("expected thrown", type.getName())
          + formatBut(effect));
    }
  }

  public void thenThrown() {
    Effect effect = getLastEffect();
    boolean expected = effect instanceof Thrown;
    if (!expected) {
      throw assertionError("\n"
          + formatSection("expected thrown", "")
          + formatBut(effect));
    }
  }

  public void then(boolean condition) {
    if (!condition) {
      throw assertionError("\n"
          + formatSection("expected", "true")
          + formatSection("but was", "false"));
    }
  }

  public void then(Object object, Object matcher) {
    if (!asMatcher(matcher).matches(object)) {
      throw assertionError("\n"
          + formatSection("expected", matcher)
          + formatSection("but was", object)
          + tryFormatDiagnosis(matcher, object));
    }
  }

  public void thenEqual(Object object, Object expected) {
    if (!deepEquals(object, expected)) {
      throw assertionError("\n"
          + formatSection("expected", expected)
          + formatSection("but was", object));
    }
  }

  public <T> T thenCalled(T mock) {
    return thenCalledTimes(exactly(1), mock);
  }

  public void thenCalled(InvocationMatcher invocationMatcher) {
    thenCalledTimes(exactly(1), invocationMatcher);
  }

  public <T> T thenCalledNever(T mock) {
    return thenCalledTimes(exactly(0), mock);
  }

  public void thenCalledNever(InvocationMatcher invocationMatcher) {
    thenCalledTimes(exactly(0), invocationMatcher);
  }

  public <T> T thenCalledTimes(int number, T mock) {
    return thenCalledTimes(exactly(number), mock);
  }

  public void thenCalledTimes(int number, InvocationMatcher invocationMatcher) {
    thenCalledTimes(exactly(number), invocationMatcher);
  }

  public <T> T thenCalledTimes(final Object numberMatcher, T mock) {
    Handler handler = new Handler() {
      public Object handle(Invocation invocation) {
        thenCalledTimes(numberMatcher, matcherize(invocation));
        return null;
      }
    };
    return proxyWrapping(mock, handler);
  }

  public void thenCalledTimes(Object numberMatcher, InvocationMatcher invocationMatcher) {
    int numberOfCalls = 0;
    for (Invocation invocation : invocationHistory.get()) {
      if (invocationMatcher.matches(invocation)) {
        numberOfCalls++;
      }
    }
    boolean expected = asMatcher(numberMatcher).matches(numberOfCalls);
    if (!expected) {
      throw assertionError("\n"
          + formatSection("expected called times " + numberMatcher, invocationMatcher)
          + formatSection("but called", "times " + numberOfCalls)
          + formatInvocations());
    }
  }

  public <T> T thenCalledInOrder(T mock) {
    Handler handler = new Handler() {
      public Object handle(Invocation invocation) {
        thenCalledInOrder(matcherize(invocation));
        return null;
      }
    };
    return proxyWrapping(mock, handler);
  }

  public void thenCalledInOrder(InvocationMatcher invocationMatcher) {
    Optional<VerifyingInOrder> verified = verifyInOrder(invocationMatcher, configuration.history.get());
    if (verified.isPresent()) {
      configuration.history.add(verified.get());
    } else {
      throw assertionError("\n"
          + formatSection("expected called in order", invocationMatcher)
          + "  but not called\n"
          + formatInvocations());
    }
  }

  private InvocationMatcher matcherize(Invocation invocation) {
    try {
      return configuration.wildcardSupport.matcherize(invocation);
    } catch (WildcardException e) {
      throw new TestoryException(e);
    }
  }

  private Effect getLastEffect() {
    return inspectingHistory.get().get().effect;
  }

  private String formatSection(String caption, Object content) {
    return ""
        + "  " + caption + "\n"
        + "    " + configuration.formatter.format(content) + "\n";
  }

  private String formatBut(Effect effect) {
    return effect instanceof Returned
        ? effect instanceof ReturnedObject
            ? formatSection("but returned", ((ReturnedObject) effect).object)
            : formatSection("but returned", "void")
        : ""
            + formatSection("but thrown", ((Thrown) effect).throwable)
            + "\n"
            + printStackTrace(((Thrown) effect).throwable);
  }

  private String formatInvocations() {
    StringBuilder builder = new StringBuilder();

    for (Object event : configuration.history.get().reverse()) {
      if (event instanceof Invocation) {
        Invocation invocation = (Invocation) event;
        builder.append("    ").append(configuration.formatter.format(invocation)).append("\n");
      }
    }
    if (builder.length() > 0) {
      builder.insert(0, "  actual invocations\n");
    } else {
      builder.insert(0, "  actual invocations\n    none\n");
    }
    return builder.toString();
  }

  private String tryFormatDiagnosis(Object matcher, Object item) {
    Matcher asMatcher = asMatcher(matcher);
    return asMatcher instanceof DiagnosticMatcher
        ? formatSection("diagnosis", ((DiagnosticMatcher) asMatcher).diagnose(item))
        : "";
  }

  private static Closure asClosure(final VoidClosure closure) {
    return new Closure() {
      public Object invoke() throws Throwable {
        closure.invoke();
        return null;
      }
    };
  }

  private static Matcher exactly(final int number) {
    return new Matcher() {
      public boolean matches(Object item) {
        return item.equals(number);
      }

      public String toString() {
        return "" + number;
      }
    };
  }

  private <T> T proxyWrapping(T wrapped, Handler handler) {
    return (T) configuration.proxer.proxy(
        subclassing(wrapped.getClass()),
        returningDefaultValue(delegatingTo(wrapped, handler)));
  }
}
