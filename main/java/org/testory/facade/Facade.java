package org.testory.facade;

import static java.util.Objects.deepEquals;
import static org.testory.TestoryAssertionError.assertionError;
import static org.testory.common.Classes.defaultValue;
import static org.testory.common.Effect.returned;
import static org.testory.common.Effect.returnedVoid;
import static org.testory.common.Effect.thrown;
import static org.testory.common.Matchers.asMatcher;
import static org.testory.common.Matchers.isMatcher;
import static org.testory.common.Throwables.gently;
import static org.testory.common.Throwables.printStackTrace;
import static org.testory.plumbing.Calling.callings;
import static org.testory.plumbing.Checker.checker;
import static org.testory.plumbing.CheckingProxer.checkingProxer;
import static org.testory.plumbing.Inspecting.inspecting;
import static org.testory.plumbing.QuietFormatter.quietFormatter;
import static org.testory.plumbing.Stubbing.stubbing;
import static org.testory.plumbing.VerifyingInOrder.verifyInOrder;
import static org.testory.plumbing.history.FilteredHistory.filter;
import static org.testory.plumbing.im.wildcard.Repairer.repairer;
import static org.testory.plumbing.im.wildcard.Tokenizer.tokenizer;
import static org.testory.plumbing.im.wildcard.WildcardMatcherizer.wildcardMatcherizer;
import static org.testory.plumbing.im.wildcard.WildcardSupport.wildcardSupport;
import static org.testory.plumbing.inject.ArrayMaker.singletonArray;
import static org.testory.plumbing.inject.ChainedMaker.chain;
import static org.testory.plumbing.inject.FinalMaker.finalMaker;
import static org.testory.plumbing.inject.RandomPrimitiveMaker.randomPrimitiveMaker;
import static org.testory.plumbing.mock.NiceMockMaker.nice;
import static org.testory.plumbing.mock.RawMockMaker.rawMockMaker;
import static org.testory.plumbing.mock.SaneMockMaker.sane;
import static org.testory.plumbing.mock.UniqueNamer.uniqueNamer;
import static org.testory.proxy.Invocation.invocation;
import static org.testory.proxy.Invocations.invoke;
import static org.testory.proxy.Typing.typing;
import static org.testory.proxy.proxer.NonFinalProxer.nonFinal;
import static org.testory.proxy.proxer.TypeSafeProxer.typeSafe;
import static org.testory.proxy.proxer.WrappingProxer.wrapping;

import java.util.HashSet;

import org.testory.TestoryException;
import org.testory.common.Closure;
import org.testory.common.DiagnosticMatcher;
import org.testory.common.Effect;
import org.testory.common.Effect.Returned;
import org.testory.common.Effect.ReturnedObject;
import org.testory.common.Effect.Thrown;
import org.testory.common.Formatter;
import org.testory.common.Matcher;
import org.testory.common.Nullable;
import org.testory.common.Optional;
import org.testory.common.VoidClosure;
import org.testory.plumbing.Calling;
import org.testory.plumbing.Checker;
import org.testory.plumbing.Inspecting;
import org.testory.plumbing.Maker;
import org.testory.plumbing.QuietFormatter;
import org.testory.plumbing.VerifyingInOrder;
import org.testory.plumbing.history.FilteredHistory;
import org.testory.plumbing.history.History;
import org.testory.plumbing.im.Matcherizer;
import org.testory.plumbing.im.wildcard.WildcardException;
import org.testory.plumbing.im.wildcard.WildcardSupport;
import org.testory.plumbing.inject.Injector;
import org.testory.plumbing.mock.Namer;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;
import org.testory.proxy.Proxer;
import org.testory.proxy.Typing;
import org.testory.proxy.proxer.CglibProxer;

public class Facade {
  private final Formatter formatter;
  private final History history;
  private final Proxer proxer;
  private final Namer mockNamer;
  private final Maker mockMaker;
  private final Injector injector;
  private final Matcherizer matcherizer;
  private final WildcardSupport wildcardSupport;
  private final FilteredHistory<Inspecting> inspectingHistory;
  private final Checker checker;

  public Facade(
      History history,
      Formatter formatter,
      Proxer proxer,
      Namer mockNamer,
      Maker mockMaker,
      Injector injector,
      FilteredHistory<Inspecting> inspectingHistory,
      WildcardSupport wildcardSupport,
      Matcherizer matcherizer,
      Checker checker) {
    this.history = history;
    this.formatter = formatter;
    this.proxer = proxer;
    this.mockNamer = mockNamer;
    this.mockMaker = mockMaker;
    this.injector = injector;
    this.inspectingHistory = inspectingHistory;
    this.wildcardSupport = wildcardSupport;
    this.matcherizer = matcherizer;
    this.checker = checker;
  }

  public static Facade newFacade(History mutableHistory) {
    Class<TestoryException> exception = TestoryException.class;
    QuietFormatter formatter = quietFormatter();
    History history = formatter.quiet(mutableHistory);
    Proxer proxer = wrapping(exception, nonFinal(typeSafe(wrapping(exception, new CglibProxer()))));
    Namer mockNamer = uniqueNamer(history);
    Checker checker = checker(history, exception);
    Maker mockMaker = mockMaker(history, checkingProxer(checker, proxer));
    Injector injector = injector(mockMaker);
    FilteredHistory<Inspecting> inspectingHistory = filter(Inspecting.class, history);
    WildcardSupport wildcardSupport = wildcardSupport(history, tokenizer(), formatter);
    Matcherizer matcherizer = wildcardMatcherizer(history, repairer(), formatter);
    return new Facade(history, formatter, proxer, mockNamer, mockMaker, injector,
        inspectingHistory, wildcardSupport, matcherizer, checker);
  }

  private static Maker mockMaker(History history, Proxer proxer) {
    Maker rawMockMaker = rawMockMaker(proxer, history);
    Maker niceMockMaker = nice(rawMockMaker, history);
    Maker saneNiceMockMaker = sane(niceMockMaker, history);
    return saneNiceMockMaker;
  }

  private static Injector injector(Maker mockMaker) {
    Maker fieldMaker = singletonArray(chain(randomPrimitiveMaker(), finalMaker(), mockMaker));
    return new Injector(fieldMaker);
  }

  public void givenTest(Object test) {
    try {
      injector.inject(test);
    } catch (RuntimeException e) {
      throw new TestoryException(e);
    }
  }

  public void given(Closure closure) {
    throw new TestoryException("\n\tgiven(Closure) is confusing, do not use it\n");
  }

  public <T> T given(T object) {
    return object;
  }

  public void given(boolean primitive) {}

  public void given(double primitive) {}

  public <T> T givenTry(T object) {
    checker.notNull(object);
    Handler handler = new Handler() {
      public Object handle(Invocation invocation) {
        try {
          return invoke(invocation);
        } catch (Throwable e) {
          return null;
        }
      }
    };
    return proxyWrapping(object, handler);
  }

  public void givenTimes(int number, Closure closure) {
    checker.notNegative(number);
    checker.notNull(closure);
    for (int i = 0; i < number; i++) {
      try {
        closure.invoke();
      } catch (Throwable throwable) {
        throw gently(throwable);
      }
    }
  }

  public <T> T givenTimes(final int number, T object) {
    checker.notNegative(number);
    checker.notNull(object);
    Handler handler = new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        for (int i = 0; i < number; i++) {
          invoke(invocation);
        }
        return null;
      }
    };
    return proxyWrapping(object, handler);
  }

  public <T> T mock(Class<T> type) {
    checker.notNull(type);
    String name = mockNamer.name(type);
    return mockMaker.make(type, name);
  }

  public <T> T spy(T real) {
    checker.notNull(real);
    Class<T> type = (Class<T>) real.getClass();
    T mock = mock(type);
    given(willSpy(real), onInstance(mock));
    return mock;
  }

  public <T> T given(final Handler handler, T mock) {
    checker.notNull(handler);
    checker.mock(mock);
    return proxyWrapping(mock, new Handler() {
      public Object handle(Invocation invocation) {
        history.add(stubbing(matcherize(invocation), handler));
        return null;
      }
    });
  }

  public void given(Handler handler, InvocationMatcher invocationMatcher) {
    checker.notNull(handler);
    checker.notNull(invocationMatcher);
    history.add(stubbing(invocationMatcher, handler));
  }

  public Handler willReturn(@Nullable final Object object) {
    return new Handler() {
      public Object handle(Invocation invocation) {
        return object;
      }
    };
  }

  public Handler willThrow(final Throwable throwable) {
    checker.notNull(throwable);
    return new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        throw throwable.fillInStackTrace();
      }
    };
  }

  public Handler willRethrow(final Throwable throwable) {
    checker.notNull(throwable);
    return new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        throw throwable;
      }
    };
  }

  public Handler willSpy(final Object real) {
    checker.notNull(real);
    return new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        return invoke(invocation(invocation.method, real, invocation.arguments));
      }
    };
  }

  public <T> T any(Class<T> type) {
    checker.notNull(type);
    return (T) wildcardSupport.any(type);
  }

  public <T> T any(Class<T> type, Object matcher) {
    checker.matcher(matcher);
    return (T) wildcardSupport.any(type, matcher);
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
    checker.notNull(value);
    return (T) wildcardSupport.a(value);
  }

  public <T> T the(T value) {
    checker.notNull(value);
    return (T) wildcardSupport.the(value);
  }

  public void the(boolean value) {
    throw new TestoryException();
  }

  public void the(double value) {
    throw new TestoryException();
  }

  public InvocationMatcher onInstance(final Object mock) {
    checker.mock(mock);
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
    checker.notNull(type);
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
    checker.notNull(type);
    checker.notNull(arguments);
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
    history.add(inspecting(returned(object)));
    try {
      return proxyWrapping(object, new Handler() {
        public Object handle(Invocation invocation) {
          history.add(inspecting(effectOf(invocation)));
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
      object = invoke(invocation);
    } catch (Throwable throwable) {
      return thrown(throwable);
    }
    return invocation.method.getReturnType() == void.class
        ? returnedVoid()
        : returned(object);
  }

  public void when(Closure closure) {
    checker.notNull(closure);
    history.add(inspecting(effectOf(closure)));
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
    checker.notNull(closure);
    history.add(inspecting(effectOf(closure)));
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

  public void thenReturned(@Nullable Object objectOrMatcher) {
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
    checker.matcher(matcher);
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
    checker.notNull(throwable);
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
    checker.notNull(type);
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

  public void then(@Nullable Object object, Object matcher) {
    checker.matcher(matcher);
    if (!asMatcher(matcher).matches(object)) {
      throw assertionError("\n"
          + formatSection("expected", matcher)
          + formatSection("but was", object)
          + tryFormatDiagnosis(matcher, object));
    }
  }

  public void thenEqual(@Nullable Object object, @Nullable Object expected) {
    if (!deepEquals(object, expected)) {
      throw assertionError("\n"
          + formatSection("expected", expected)
          + formatSection("but was", object));
    }
  }

  public <T> T thenCalled(T mock) {
    checker.mock(mock);
    return thenCalledTimes(exactly(1), mock);
  }

  public void thenCalled(InvocationMatcher invocationMatcher) {
    checker.notNull(invocationMatcher);
    thenCalledTimes(exactly(1), invocationMatcher);
  }

  public <T> T thenCalledNever(T mock) {
    checker.mock(mock);
    return thenCalledTimes(exactly(0), mock);
  }

  public void thenCalledNever(InvocationMatcher invocationMatcher) {
    checker.notNull(invocationMatcher);
    thenCalledTimes(exactly(0), invocationMatcher);
  }

  public <T> T thenCalledTimes(int number, T mock) {
    checker.notNegative(number);
    checker.mock(mock);
    return thenCalledTimes(exactly(number), mock);
  }

  public void thenCalledTimes(int number, InvocationMatcher invocationMatcher) {
    checker.notNegative(number);
    checker.notNull(invocationMatcher);
    thenCalledTimes(exactly(number), invocationMatcher);
  }

  public <T> T thenCalledTimes(final Object numberMatcher, T mock) {
    checker.matcher(numberMatcher);
    checker.mock(mock);
    Handler handler = new Handler() {
      public Object handle(Invocation invocation) {
        thenCalledTimes(numberMatcher, matcherize(invocation));
        return null;
      }
    };
    return proxyWrapping(mock, handler);
  }

  public void thenCalledTimes(Object numberMatcher, InvocationMatcher invocationMatcher) {
    checker.matcher(numberMatcher);
    checker.notNull(invocationMatcher);
    int numberOfCalls = 0;
    for (Calling calling : callings(history.get())) {
      if (invocationMatcher.matches(calling.invocation)) {
        numberOfCalls++;
      }
    }
    boolean expected = asMatcher(numberMatcher).matches(numberOfCalls);
    if (!expected) {
      throw assertionError("\n"
          + formatSection("expected called times " + numberMatcher, invocationMatcher)
          + formatSection("but called", "times " + numberOfCalls)
          + formatCallings());
    }
  }

  public <T> T thenCalledInOrder(T mock) {
    checker.mock(mock);
    Handler handler = new Handler() {
      public Object handle(Invocation invocation) {
        thenCalledInOrder(matcherize(invocation));
        return null;
      }
    };
    return proxyWrapping(mock, handler);
  }

  public void thenCalledInOrder(InvocationMatcher invocationMatcher) {
    checker.notNull(invocationMatcher);
    Optional<VerifyingInOrder> verified = verifyInOrder(invocationMatcher, history.get());
    if (verified.isPresent()) {
      history.add(verified.get());
    } else {
      throw assertionError("\n"
          + formatSection("expected called in order", invocationMatcher)
          + "  but not called\n"
          + formatCallings());
    }
  }

  private InvocationMatcher matcherize(Invocation invocation) {
    try {
      return matcherizer.matcherize(invocation);
    } catch (WildcardException e) {
      throw new TestoryException(e);
    }
  }

  private Effect getLastEffect() {
    checker.mustCallWhen();
    return inspectingHistory.get().get().effect;
  }

  private String formatSection(String caption, @Nullable Object content) {
    return ""
        + "  " + caption + "\n"
        + "    " + formatter.format(content) + "\n";
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

  private String formatCallings() {
    StringBuilder builder = new StringBuilder();

    for (Object event : history.get().reverse()) {
      if (event instanceof Calling) {
        Calling calling = (Calling) event;
        Invocation invocation = calling.invocation;
        builder.append("    ").append(formatter.format(invocation)).append("\n");
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
    Typing typing = typing(wrapped.getClass(), new HashSet<Class<?>>());
    Handler proxyHandler = returningDefaultValue(delegatingTo(wrapped, handler));
    return (T) proxer.proxy(typing, proxyHandler);
  }

  private static Handler returningDefaultValue(final Handler handler) {
    return new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        handler.handle(invocation);
        return defaultValue(invocation.method.getReturnType());
      }
    };
  }

  private static Handler delegatingTo(final Object instance, final Handler handler) {
    return new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        return handler.handle(invocation(invocation.method, instance, invocation.arguments));
      }
    };
  }
}
