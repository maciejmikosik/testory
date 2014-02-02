package org.testory;

import static org.testory.common.Checks.checkNotNull;
import static org.testory.common.Objects.print;
import static org.testory.util.Primitives.zeroOrNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testory.common.Nullable;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
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
  }

  public <T> T logCaptor(Class<?> type) {
    Captor captor = new Captor();
    captor.type = type;
    addEvent(captor);
    return (T) zeroOrNull(type);
  }

  public On buildOnUsingCaptors(final Invocation invocation) {
    final List<Captor> captors = getCaptorsAndConsume();
    if (captors.size() > 0) {
      check(captors.size() == invocation.arguments.size());
    }
    return new On() {
      public boolean matches(Invocation item) {
        return invocation.instance == item.instance && invocation.method.equals(item.method)
            && captors.size() > 0 || invocation.equals(item);
      }

      public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(invocation.instance);
        builder.append(".");
        builder.append(invocation.method.getName());
        builder.append("(");
        if (captors.size() > 0) {
          for (Captor captor : captors) {
            builder.append("any(").append(captor.type.getName()).append("), ");
            builder.delete(builder.length() - 2, builder.length());
          }
        } else {
          for (Object argument : invocation.arguments) {
            builder.append(print(argument)).append(", ");
          }
          if (invocation.arguments.size() > 0) {
            builder.delete(builder.length() - 2, builder.length());
          }
        }
        builder.append(")");
        return builder.toString();
      }
    };
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
