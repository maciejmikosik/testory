package org.testory.plumbing.facade;

import static java.util.Objects.deepEquals;
import static org.testory.TestoryAssertionError.assertionError;
import static org.testory.common.Classes.defaultValue;
import static org.testory.common.Effect.returned;
import static org.testory.common.Effect.returnedVoid;
import static org.testory.common.Effect.thrown;
import static org.testory.common.Matchers.asDiagnosticMatcher;
import static org.testory.common.Matchers.asMatcher;
import static org.testory.common.Matchers.isDiagnosticMatcher;
import static org.testory.common.Matchers.isMatcher;
import static org.testory.common.Throwables.gently;
import static org.testory.common.Throwables.printStackTrace;
import static org.testory.plumbing.Inspecting.inspecting;
import static org.testory.plumbing.PlumbingException.check;
import static org.testory.plumbing.format.Body.body;
import static org.testory.plumbing.format.Header.header;
import static org.testory.plumbing.history.FilteredHistory.filter;
import static org.testory.plumbing.mock.Stubbed.stubbed;
import static org.testory.proxy.Invocation.invocation;
import static org.testory.proxy.handler.ReturningHandler.returning;
import static org.testory.proxy.handler.ThrowingHandler.throwing;

import org.testory.common.Closure;
import org.testory.common.Effect;
import org.testory.common.Effect.ReturnedObject;
import org.testory.common.Effect.ReturnedVoid;
import org.testory.common.Effect.Thrown;
import org.testory.common.VoidClosure;
import org.testory.plumbing.Inspecting;
import org.testory.plumbing.history.FilteredHistory;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class ConfigurableFacade implements Facade {
  private final Configuration configuration;
  private final FilteredHistory<Inspecting> inspectingHistory;

  private ConfigurableFacade(Configuration configuration) {
    this.configuration = configuration;
    inspectingHistory = filter(Inspecting.class, configuration.history);
  }

  public static Facade configurableFacade(Configuration configuration) {
    check(configuration != null);
    return new ConfigurableFacade(configuration.validate());
  }

  public void givenTest(Object test) {
    try {
      configuration.injector.inject(test);
    } catch (RuntimeException e) {
      throw configuration.checker.wrap(e);
    }
  }

  public void given(Closure closure) {
    try {
      closure.invoke();
    } catch (Throwable e) {
      throw configuration.checker.wrap(e);
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
    return configuration.overrider.override(object, new Handler() {
      public Object handle(Invocation invocation) {
        try {
          return invocation.invoke();
        } catch (Throwable e) {
          return null;
        }
      }
    });
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
    return configuration.overrider.override(object, new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        for (int i = 0; i < number; i++) {
          invocation.invoke();
        }
        return defaultValue(invocation.method.getReturnType());
      }
    });
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
    return configuration.overrider.override(mock, new Handler() {
      public Object handle(Invocation invocation) {
        configuration.history.add(stubbed(configuration.wildcardSupport.matcherize(invocation), handler));
        return defaultValue(invocation.method.getReturnType());
      }
    });
  }

  public void given(Handler handler, InvocationMatcher invocationMatcher) {
    configuration.history.add(stubbed(invocationMatcher, handler));
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
    configuration.checker.fail("unsupported");
  }

  public void the(double value) {
    configuration.checker.fail("unsupported");
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
      return configuration.overrider.override(object, new Handler() {
        public Object handle(Invocation invocation) {
          configuration.history.add(inspecting(effectOf(invocation)));
          return defaultValue(invocation.method.getReturnType());
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
    if (effect instanceof Thrown) {
      Thrown thrown = (Thrown) effect;
      throw assertionError(configuration.pageFormatter
          .add(header("expected returned"))
          .add(body(objectOrMatcher))
          .add(header("but thrown"))
          .add(body(thrown.throwable))
          .add("\n")
          .add(printStackTrace(thrown.throwable))
          .build());
    } else if (effect instanceof ReturnedVoid) {
      throw assertionError(configuration.pageFormatter
          .add(header("expected returned"))
          .add(body(objectOrMatcher))
          .add(header("but returned"))
          .add(body("void"))
          .build());
    }

    ReturnedObject returned = (ReturnedObject) effect;
    if (deepEquals(objectOrMatcher, returned.object)) {
      return;
    } else if (objectOrMatcher != null
        && isMatcher(objectOrMatcher)
        && asMatcher(objectOrMatcher).matches(returned.object)) {
      return;
    } else if (objectOrMatcher != null && isDiagnosticMatcher(objectOrMatcher)) {
      throw assertionError(configuration.pageFormatter
          .add(header("expected returned"))
          .add(body(objectOrMatcher))
          .add(header("but returned"))
          .add(body(returned.object))
          .add(header("diagnosis"))
          .add(body(asDiagnosticMatcher(objectOrMatcher).diagnose(returned.object)))
          .build());
    } else {
      throw assertionError(configuration.pageFormatter
          .add(header("expected returned"))
          .add(body(objectOrMatcher))
          .add(header("but returned"))
          .add(body(returned.object))
          .build());
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
    if (effect instanceof Thrown) {
      Thrown thrown = (Thrown) effect;
      throw assertionError(configuration.pageFormatter
          .add(header("expected returned"))
          .add(body(""))
          .add(header("but thrown"))
          .add(body(thrown.throwable))
          .add("\n")
          .add(printStackTrace(thrown.throwable))
          .build());
    }
  }

  public void thenThrown(Object matcher) {
    Effect effect = getLastEffect();
    if (effect instanceof ReturnedObject) {
      throw assertionError(configuration.pageFormatter
          .add(header("expected thrown"))
          .add(body(matcher))
          .add(header("but returned"))
          .add(body(((ReturnedObject) effect).object))
          .build());
    } else if (effect instanceof ReturnedVoid) {
      throw assertionError(configuration.pageFormatter
          .add(header("expected thrown"))
          .add(body(matcher))
          .add(header("but returned"))
          .add(body("void"))
          .build());
    }

    Thrown thrown = (Thrown) effect;
    if (asMatcher(matcher).matches(thrown.throwable)) {
      return;
    } else if (isDiagnosticMatcher(matcher)) {
      throw assertionError(configuration.pageFormatter
          .add(header("expected thrown"))
          .add(body(matcher))
          .add(header("but thrown"))
          .add(body(thrown.throwable))
          .add(header("diagnosis"))
          .add(body(asDiagnosticMatcher(matcher).diagnose(thrown.throwable)))
          .add("\n")
          .add(printStackTrace(thrown.throwable))
          .build());
    } else {
      throw assertionError(configuration.pageFormatter
          .add(header("expected thrown"))
          .add(body(matcher))
          .add(header("but thrown"))
          .add(body(thrown.throwable))
          .add("\n")
          .add(printStackTrace(thrown.throwable))
          .build());
    }
  }

  public void thenThrown(Throwable throwable) {
    Effect effect = getLastEffect();
    if (effect instanceof ReturnedObject) {
      throw assertionError(configuration.pageFormatter
          .add(header("expected thrown"))
          .add(body(throwable))
          .add(header("but returned"))
          .add(body(((ReturnedObject) effect).object))
          .build());
    } else if (effect instanceof ReturnedVoid) {
      throw assertionError(configuration.pageFormatter
          .add(header("expected thrown"))
          .add(body(throwable))
          .add(header("but returned"))
          .add(body("void"))
          .build());
    }

    Thrown thrown = (Thrown) effect;
    if (deepEquals(throwable, thrown.throwable)) {
      return;
    } else {
      throw assertionError(configuration.pageFormatter
          .add(header("expected thrown"))
          .add(body(throwable))
          .add(header("but thrown"))
          .add(body(thrown.throwable))
          .add("\n")
          .add(printStackTrace(thrown.throwable))
          .build());
    }
  }

  public void thenThrown(Class<? extends Throwable> type) {
    Effect effect = getLastEffect();
    if (effect instanceof ReturnedObject) {
      throw assertionError(configuration.pageFormatter
          .add(header("expected thrown"))
          .add(body(type.getName()))
          .add(header("but returned"))
          .add(body(((ReturnedObject) effect).object))
          .build());
    } else if (effect instanceof ReturnedVoid) {
      throw assertionError(configuration.pageFormatter
          .add(header("expected thrown"))
          .add(body(type.getName()))
          .add(header("but returned"))
          .add(body("void"))
          .build());
    }

    Thrown thrown = (Thrown) effect;
    if (type.isInstance(thrown.throwable)) {
      return;
    } else {
      throw assertionError(configuration.pageFormatter
          .add(header("expected thrown"))
          .add(body(type.getName()))
          .add(header("but thrown"))
          .add(body(thrown.throwable))
          .add("\n")
          .add(printStackTrace(thrown.throwable))
          .build());
    }
  }

  public void thenThrown() {
    Effect effect = getLastEffect();
    if (effect instanceof ReturnedObject) {
      throw assertionError(configuration.pageFormatter
          .add(header("expected thrown"))
          .add(body(""))
          .add(header("but returned"))
          .add(body(((ReturnedObject) effect).object))
          .build());
    } else if (effect instanceof ReturnedVoid) {
      throw assertionError(configuration.pageFormatter
          .add(header("expected thrown"))
          .add(body(""))
          .add(header("but returned"))
          .add(body("void"))
          .build());
    }
  }

  public void then(boolean condition) {
    if (!condition) {
      throw assertionError(configuration.pageFormatter
          .add(header("expected"))
          .add(body(true))
          .add(header("but was"))
          .add(body(false))
          .build());
    }
  }

  public void then(Object object, Object matcher) {
    if (asMatcher(matcher).matches(object)) {
      return;
    } else if (isDiagnosticMatcher(matcher)) {
      throw assertionError(configuration.pageFormatter
          .add(header("expected"))
          .add(body(matcher))
          .add(header("but was"))
          .add(body(object))
          .add(header("diagnosis"))
          .add(body(asDiagnosticMatcher(matcher).diagnose(object)))
          .build());
    } else {
      throw assertionError(configuration.pageFormatter
          .add(header("expected"))
          .add(body(matcher))
          .add(header("but was"))
          .add(body(object))
          .build());
    }
  }

  public void thenEqual(Object object, Object expected) {
    if (!deepEquals(object, expected)) {
      throw assertionError(configuration.pageFormatter
          .add(header("expected"))
          .add(body(expected))
          .add(header("but was"))
          .add(body(object))
          .build());
    }
  }

  public <T> T thenCalled(T mock) {
    return configuration.verifier.thenCalled(mock);
  }

  public void thenCalled(InvocationMatcher invocationMatcher) {
    configuration.verifier.thenCalled(invocationMatcher);
  }

  public <T> T thenCalledNever(T mock) {
    return configuration.verifier.thenCalledNever(mock);
  }

  public void thenCalledNever(InvocationMatcher invocationMatcher) {
    configuration.verifier.thenCalledNever(invocationMatcher);
  }

  public <T> T thenCalledTimes(int number, T mock) {
    return configuration.verifier.thenCalledTimes(number, mock);
  }

  public void thenCalledTimes(int number, InvocationMatcher invocationMatcher) {
    configuration.verifier.thenCalledTimes(number, invocationMatcher);
  }

  public <T> T thenCalledTimes(Object numberMatcher, T mock) {
    return configuration.verifier.thenCalledTimes(numberMatcher, mock);
  }

  public void thenCalledTimes(Object numberMatcher, InvocationMatcher invocationMatcher) {
    configuration.verifier.thenCalledTimes(numberMatcher, invocationMatcher);
  }

  public <T> T thenCalledInOrder(T mock) {
    return configuration.verifier.thenCalledInOrder(mock);
  }

  public void thenCalledInOrder(InvocationMatcher invocationMatcher) {
    configuration.verifier.thenCalledInOrder(invocationMatcher);
  }

  private Effect getLastEffect() {
    return inspectingHistory.get().get().effect;
  }

  private static Closure asClosure(final VoidClosure closure) {
    return new Closure() {
      public Object invoke() throws Throwable {
        closure.invoke();
        return null;
      }
    };
  }
}
