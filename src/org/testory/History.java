package org.testory;

import static org.testory.common.Checks.checkNotNull;
import static org.testory.common.Objects.areEqualDeep;
import static org.testory.common.Objects.print;
import static org.testory.proxy.Proxies.isProxiable;
import static org.testory.proxy.Proxies.proxy;
import static org.testory.proxy.Typing.typing;
import static org.testory.util.Matchers.match;
import static org.testory.util.Primitives.zeroOrNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.testory.common.Nullable;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.Typing;
import org.testory.util.Effect;

class History {
  /** youngest at begin */
  private final ThreadLocal<List<Object>> threadLocalEvents = new ThreadLocal<List<Object>>() {
    protected List<Object> initialValue() {
      return new ArrayList<Object>();
    }
  };

  public History() {}

  private List<Object> getEvents() {
    return new ArrayList<Object>(threadLocalEvents.get());
  }

  private void setEvents(List<Object> events) {
    threadLocalEvents.set(new ArrayList<Object>(events));
  }

  private void addEvent(Object event) {
    threadLocalEvents.get().add(0, event);
  }

  private static class Purge {}

  public void purge() {
    List<Object> events = getEvents();
    for (int i = 0; i < events.size(); i++) {
      if (events.get(i) instanceof Purge) {
        setEvents(events.subList(0, i));
        break;
      }
    }
    addEvent(new Purge());
  }

  public void logWhen(Effect effect) {
    checkNotNull(effect);
    addEvent(effect);
  }

  public Effect getLastWhenEffect() {
    Effect effect = null;
    for (Object event : getEvents()) {
      if (event instanceof Effect) {
        effect = (Effect) event;
        break;
      }
    }
    check(effect != null);
    return effect;
  }

  private static void check(boolean condition) {
    if (!condition) {
      throw new TestoryException();
    }
  }

  private static class Stubbing {
    Handler handler;
    On on;
  }

  public void logStubbing(Handler handler, On on) {
    Stubbing stubbing = new Stubbing();
    stubbing.handler = handler;
    stubbing.on = on;
    addEvent(stubbing);
  }

  @Nullable
  public Handler getStubbedHandlerFor(Invocation invocation) {
    for (Object event : getEvents()) {
      if (event instanceof Stubbing) {
        Stubbing stubbing = (Stubbing) event;
        if (stubbing.on.matches(invocation)) {
          return stubbing.handler;
        }
      }
    }
    return null;
  }

  public void logInvocation(Invocation invocation) {
    addEvent(invocation);
  }

  public List<Invocation> getInvocations() {
    List<Invocation> invocations = new ArrayList<Invocation>();
    for (Object event : getEvents()) {
      if (event instanceof Invocation) {
        invocations.add((Invocation) event);
      }
    }
    return Collections.unmodifiableList(invocations);
  }

  private static class Captor {
    Class<?> type;
    @Nullable
    Object token;
    @Nullable
    Object matcher;
  }

  public <T> T logCaptor(Class<T> type, @Nullable Object matcher) {
    boolean isProxiable = isProxiable(type);
    T token = isProxiable
        ? proxyLight(type)
        : null;

    Captor captor = new Captor();
    captor.type = type;
    captor.token = token;
    captor.matcher = matcher;
    addEvent(captor);

    return isProxiable
        ? token
        : zeroOrNull(type);
  }

  private static <T> T proxyLight(Class<T> type) {
    Typing typing = type.isInterface()
        ? typing(Object.class, new HashSet<Class<?>>(Arrays.asList(type)))
        : typing(type, new HashSet<Class<?>>());
    Handler handler = new Handler() {
      @Nullable
      public Object handle(Invocation invocation) throws Throwable {
        return null;
      }
    };
    return (T) proxy(typing, handler);
  }

  public On buildOnUsingCaptors(final Invocation invocation) {
    final List<Captor> captors = getCaptorsAndConsume();
    final List<Object> argumentMatchers = solve(invocation.arguments, captors);
    final Object argumentsMatcher = new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object item) {
        List<Object> arguments = (List<Object>) item;
        for (int i = 0; i < argumentMatchers.size(); i++) {
          if (!match(argumentMatchers.get(i), arguments.get(i))) {
            return false;
          }
        }
        return true;
      }
    };

    return new On() {
      public boolean matches(Invocation item) {
        return invocation.instance == item.instance && invocation.method.equals(item.method)
            && match(argumentsMatcher, item.arguments);
      }

      public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(dangerouslyInvokeToStringOnMock(invocation.instance));
        builder.append(".");
        builder.append(invocation.method.getName());
        builder.append("(");
        for (Object argumentMatcher : argumentMatchers) {
          builder.append(argumentMatcher).append(", ");
        }
        if (argumentMatchers.size() > 0) {
          builder.delete(builder.length() - 2, builder.length());
        }
        builder.append(")");
        return builder.toString();
      }
    };
  }

  private static List<Object> solve(List<Object> arguments, List<Captor> captors) {
    for (int i = 0; i < captors.size(); i++) {
      Captor captor = captors.get(i);
      if (captor.token != null) {
        for (int j = 0; j < arguments.size(); j++) {
          Object argument = arguments.get(j);
          if (argument == captor.token) {
            ArrayList<Object> solved = new ArrayList<Object>();
            List<Object> leftArguments = arguments.subList(0, j);
            List<Object> rightArguments = arguments.subList(j + 1, arguments.size());
            List<Captor> leftCaptors = captors.subList(0, i);
            List<Captor> rightCaptors = captors.subList(i + 1, captors.size());
            solved.addAll(solve(leftArguments, leftCaptors));
            solved.add(asMatcher(captor));
            solved.addAll(solve(rightArguments, rightCaptors));
            return solved;
          }
        }
        throw new TestoryException("captor created but not passed to invocation");
      }
    }
    if (arguments.isEmpty()) {
      return new ArrayList<Object>();
    }
    if (captors.isEmpty()) {
      ArrayList<Object> solved = new ArrayList<Object>();
      for (final Object argument : arguments) {
        solved.add(new Object() {
          @SuppressWarnings("unused")
          public boolean matches(Object item) {
            return areEqualDeep(argument, item);
          }

          public String toString() {
            return print(argument);
          }
        });
      }
      return solved;
    } else if (captors.size() == arguments.size()) {
      ArrayList<Object> solved = new ArrayList<Object>();
      for (Captor captor : captors) {
        solved.add(asMatcher(captor));
      }
      return solved;
    } else {
      throw new TestoryException("cannot solve mixed arguments and captors");
    }
  }

  private static Object asMatcher(final Captor captor) {
    return new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object item) {
        return captor.matcher != null
            ? match(captor.matcher, item)
            : true;
      }

      public String toString() {
        return captor.matcher != null
            ? "any(" + captor.type.getName() + ", " + captor.matcher + ")"
            : "any(" + captor.type.getName() + ")";
      }
    };
  }

  /**
   * Invoked only in specific situation if we know that test is failing.
   * {@link Testory#thenCalledTimes(Object, On)} assertion fails, it builds error message, it
   * invokes {@link On#toString()}, it invokes this method, it invokes mock.toString(). Invoking in
   * other situations may cause not intended consequences like extra invocation to be registered and
   * verification fails.
   */

  private static String dangerouslyInvokeToStringOnMock(Object mock) {
    return mock.toString();
  }

  private List<Captor> getCaptorsAndConsume() {
    class Consumer {}
    List<Captor> captors = new ArrayList<Captor>();
    for (Object event : getEvents()) {
      if (event instanceof Captor) {
        captors.add(0, (Captor) event);
      } else if (event instanceof Consumer) {
        break;
      }
    }
    addEvent(new Consumer());
    return captors;
  }
}
