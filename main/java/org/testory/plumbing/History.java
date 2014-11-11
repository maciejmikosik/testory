package org.testory.plumbing;

import static org.testory.common.Collections.reverse;
import static org.testory.plumbing.PlumbingException.check;

import java.util.ArrayList;
import java.util.List;

import org.testory.common.Nullable;
import org.testory.proxy.Invocation;
import org.testory.util.Effect;
import org.testory.util.any.Any;

public class History {
  List<Object> events = new ArrayList<Object>();

  public History() {}

  private History(List<Object> events) {
    this.events = events;
  }

  public static History history(List<Object> events) {
    check(events != null);
    return new History(events);
  }

  private List<Object> getEvents() {
    return new ArrayList<Object>(events);
  }

  private void addEvent(Object event) {
    events.add(event);
  }

  public void logWhen(Effect effect) {
    check(effect != null);
    addEvent(effect);
  }

  public boolean hasLastWhenEffect() {
    return tryGetLastWhenEffect() != null;
  }

  public Effect getLastWhenEffect() {
    Effect effect = tryGetLastWhenEffect();
    check(effect != null);
    return effect;
  }

  @Nullable
  private Effect tryGetLastWhenEffect() {
    Effect effect = null;
    for (Object event : reverse(getEvents())) {
      if (event instanceof Effect) {
        effect = (Effect) event;
        break;
      }
    }
    return effect;
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
    return invocations;
  }

  public void logAny(Any any) {
    addEvent(any);
  }

  public List<Any> getAnysAndConsume() {
    class Consumer {}
    List<Any> anys = new ArrayList<Any>();
    for (Object event : reverse(getEvents())) {
      if (event instanceof Any) {
        anys.add(0, (Any) event);
      } else if (event instanceof Consumer) {
        break;
      }
    }
    addEvent(new Consumer());
    return anys;
  }
}
