package org.testory.facade;

import static java.util.Objects.deepEquals;
import static org.testory.TestoryAssertionError.assertionError;
import static org.testory.TestoryException.check;
import static org.testory.common.Classes.defaultValue;
import static org.testory.common.Effect.returned;
import static org.testory.common.Effect.returnedVoid;
import static org.testory.common.Effect.thrown;
import static org.testory.common.Matchers.asMatcher;
import static org.testory.common.Matchers.isMatcher;
import static org.testory.common.Throwables.gently;
import static org.testory.common.Throwables.printStackTrace;
import static org.testory.facade.MockProxer.mockProxer;
import static org.testory.plumbing.Calling.callings;
import static org.testory.plumbing.Formatter.formatter;
import static org.testory.plumbing.Inspecting.findLastInspecting;
import static org.testory.plumbing.Inspecting.inspecting;
import static org.testory.plumbing.Stubbing.stubbing;
import static org.testory.plumbing.VerifyingInOrder.verifyInOrder;
import static org.testory.plumbing.capture.AnySupport.anySupport;
import static org.testory.plumbing.capture.Repairer.repairer;
import static org.testory.plumbing.history.FilteredHistory.filter;
import static org.testory.plumbing.inject.ArrayMaker.singletonArray;
import static org.testory.plumbing.inject.ChainedMaker.chain;
import static org.testory.plumbing.inject.FinalMaker.finalMaker;
import static org.testory.plumbing.inject.PrimitiveMaker.randomPrimitiveMaker;
import static org.testory.plumbing.mock.NiceMockMaker.nice;
import static org.testory.plumbing.mock.RawMockMaker.rawMockMaker;
import static org.testory.plumbing.mock.SaneMockMaker.sane;
import static org.testory.plumbing.mock.UniqueNamer.uniqueNamer;
import static org.testory.proxy.Invocation.invocation;
import static org.testory.proxy.Invocations.invoke;
import static org.testory.proxy.Typing.typing;

import java.util.HashSet;

import org.testory.TestoryException;
import org.testory.common.Closure;
import org.testory.common.DiagnosticMatcher;
import org.testory.common.Effect;
import org.testory.common.Effect.Returned;
import org.testory.common.Effect.ReturnedObject;
import org.testory.common.Effect.Thrown;
import org.testory.common.Matcher;
import org.testory.common.Nullable;
import org.testory.common.Optional;
import org.testory.common.VoidClosure;
import org.testory.plumbing.Calling;
import org.testory.plumbing.Formatter;
import org.testory.plumbing.Inspecting;
import org.testory.plumbing.Maker;
import org.testory.plumbing.Mocking;
import org.testory.plumbing.VerifyingInOrder;
import org.testory.plumbing.capture.AnyException;
import org.testory.plumbing.capture.AnySupport;
import org.testory.plumbing.capture.Capturer;
import org.testory.plumbing.history.FilteredHistory;
import org.testory.plumbing.history.History;
import org.testory.plumbing.inject.Injector;
import org.testory.plumbing.mock.Namer;
import org.testory.proxy.CglibProxer;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;
import org.testory.proxy.Proxer;
import org.testory.proxy.Typing;

public class Facade {
  private final Formatter formatter;
  private final History history;
  private final Proxer proxer;
  private final Namer mockNamer;
  private final Maker mockMaker;
  private final Injector injector;
  private final Capturer capturer;
  private final AnySupport anySupport;
  private final FilteredHistory<Mocking> mockingHistory;

  public Facade(
      History history,
      Formatter formatter,
      Proxer proxer,
      Namer mockNamer,
      Maker mockMaker,
      Injector injector,
      FilteredHistory<Mocking> mockingHistory,
      AnySupport anySupport,
      Capturer capturer) {
    this.history = history;
    this.formatter = formatter;
    this.proxer = proxer;
    this.mockNamer = mockNamer;
    this.mockMaker = mockMaker;
    this.injector = injector;
    this.mockingHistory = mockingHistory;
    this.anySupport = anySupport;
    this.capturer = capturer;
  }

  public static Facade newFacade(History mutableHistory) {
    Formatter formatter = formatter();
    History history = formatter.plug(mutableHistory);
    Proxer proxer = new CglibProxer();
    Namer mockNamer = uniqueNamer(history);
    Maker mockMaker = mockMaker(history, proxer);
    Injector injector = injector(mockMaker);
    FilteredHistory<Mocking> mockingHistory = filter(Mocking.class, history);
    AnySupport anySupport = anySupport(history, repairer());
    Capturer capturer = anySupport.getCapturer();
    return new Facade(history, formatter, proxer, mockNamer, mockMaker, injector,
        mockingHistory, anySupport, capturer);
  }

  private static Maker mockMaker(History history, Proxer proxer) {
    Proxer mockProxer = mockProxer(history, proxer);
    Maker rawMockMaker = rawMockMaker(mockProxer, history);
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
    check(object != null);
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
    check(number >= 0);
    check(closure != null);
    for (int i = 0; i < number; i++) {
      try {
        closure.invoke();
      } catch (Throwable throwable) {
        throw gently(throwable);
      }
    }
  }

  public <T> T givenTimes(final int number, T object) {
    check(number >= 0);
    check(object != null);
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
    check(type != null);
    String name = mockNamer.name(type);
    return mockMaker.make(type, name);
  }

  public <T> T spy(T real) {
    check(real != null);
    Class<T> type = (Class<T>) real.getClass();
    T mock = mock(type);
    given(willSpy(real), onInstance(mock));
    return mock;
  }

  public <T> T given(final Handler handler, T mock) {
    check(handler != null);
    check(mock != null);
    check(isMock(mock));
    return proxyWrapping(mock, new Handler() {
      public Object handle(Invocation invocation) {
        history.add(stubbing(capture(invocation), handler));
        return null;
      }
    });
  }

  public void given(Handler handler, InvocationMatcher invocationMatcher) {
    check(handler != null);
    check(invocationMatcher != null);
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
    check(throwable != null);
    return new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        throw throwable.fillInStackTrace();
      }
    };
  }

  public Handler willRethrow(final Throwable throwable) {
    check(throwable != null);
    return new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        throw throwable;
      }
    };
  }

  public Handler willSpy(final Object real) {
    check(real != null);
    return new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        return invoke(invocation(invocation.method, real, invocation.arguments));
      }
    };
  }

  public <T> T any(Class<T> type) {
    check(type != null);
    return (T) anySupport.any(type);
  }

  public <T> T any(Class<T> type, Object matcher) {
    check(matcher != null);
    check(isMatcher(matcher));
    return (T) anySupport.any(type, matcher);
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
    check(value != null);
    return (T) anySupport.a(value);
  }

  public <T> T the(T value) {
    check(value != null);
    return (T) anySupport.the(value);
  }

  public void the(boolean value) {
    throw new TestoryException();
  }

  public void the(double value) {
    throw new TestoryException();
  }

  public InvocationMatcher onInstance(final Object mock) {
    check(mock != null);
    check(isMock(mock));
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
    check(type != null);
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
    check(type != null);
    check(arguments != null);
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
    check(closure != null);
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
    check(closure != null);
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
    check(matcher != null);
    check(isMatcher(matcher));
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
    check(throwable != null);
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
    check(type != null);
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
    check(matcher != null);
    check(isMatcher(matcher));
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
    check(mock != null);
    check(isMock(mock));
    return thenCalledTimes(exactly(1), mock);
  }

  public void thenCalled(InvocationMatcher invocationMatcher) {
    check(invocationMatcher != null);
    thenCalledTimes(exactly(1), invocationMatcher);
  }

  public <T> T thenCalledNever(T mock) {
    check(mock != null);
    check(isMock(mock));
    return thenCalledTimes(exactly(0), mock);
  }

  public void thenCalledNever(InvocationMatcher invocationMatcher) {
    check(invocationMatcher != null);
    thenCalledTimes(exactly(0), invocationMatcher);
  }

  public <T> T thenCalledTimes(int number, T mock) {
    check(number >= 0);
    check(mock != null);
    check(isMock(mock));
    return thenCalledTimes(exactly(number), mock);
  }

  public void thenCalledTimes(int number, InvocationMatcher invocationMatcher) {
    check(number >= 0);
    check(invocationMatcher != null);
    thenCalledTimes(exactly(number), invocationMatcher);
  }

  public <T> T thenCalledTimes(final Object numberMatcher, T mock) {
    check(numberMatcher != null);
    check(isMatcher(numberMatcher));
    check(mock != null);
    check(isMock(mock));
    Handler handler = new Handler() {
      public Object handle(Invocation invocation) {
        thenCalledTimes(numberMatcher, capture(invocation));
        return null;
      }
    };
    return proxyWrapping(mock, handler);
  }

  public void thenCalledTimes(Object numberMatcher, InvocationMatcher invocationMatcher) {
    check(numberMatcher != null);
    check(isMatcher(numberMatcher));
    check(invocationMatcher != null);
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
    check(mock != null);
    check(isMock(mock));
    Handler handler = new Handler() {
      public Object handle(Invocation invocation) {
        thenCalledInOrder(capture(invocation));
        return null;
      }
    };
    return proxyWrapping(mock, handler);
  }

  public void thenCalledInOrder(InvocationMatcher invocationMatcher) {
    check(invocationMatcher != null);
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

  private boolean isMock(Object instance) {
    for (Mocking mocking : mockingHistory.get()) {
      if (mocking.mock == instance) {
        return true;
      }
    }
    return false;
  }

  private InvocationMatcher capture(Invocation invocation) {
    try {
      return capturer.capture(invocation);
    } catch (AnyException e) {
      throw new TestoryException(e);
    }
  }

  private Effect getLastEffect() {
    Optional<Inspecting> inspecting = findLastInspecting(history.get());
    check(inspecting.isPresent());
    return inspecting.get().effect;
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

  private <T> T proxyWrapping(final T wrapped, final Handler handler) {
    Typing typing = typing(wrapped.getClass(), new HashSet<Class<?>>());
    Handler proxyHandler = returningDefaultValue(delegatingTo(wrapped, handler));
    try {
      return (T) proxer.proxy(typing, proxyHandler);
    } catch (RuntimeException e) {
      throw new TestoryException(e);
    }
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
