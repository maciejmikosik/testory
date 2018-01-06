package org.testory.facade;

import static org.testory.common.Chain.chain;
import static org.testory.plumbing.PlumbingException.check;

import org.testory.common.Chain;
import org.testory.common.Closure;
import org.testory.common.VoidClosure;
import org.testory.plumbing.history.History;
import org.testory.proxy.Handler;
import org.testory.proxy.InvocationMatcher;

public class PurgingFacade implements Facade {
  private final History history;
  private final Facade facade;

  private PurgingFacade(History history, Facade facade) {
    this.history = history;
    this.facade = facade;
  }

  public static Facade purging(History history, Facade facade) {
    check(history != null);
    check(facade != null);
    return new PurgingFacade(history, facade);
  }

  private enum Word {
    GIVEN, WHEN, THEN
  }

  private Word lastWord = Word.GIVEN;
  private Chain<Object> events = chain();

  private void given() {
    if (lastWord != Word.GIVEN) {
      history.cut(events);
    }
    lastWord = Word.GIVEN;
    events = history.get();
  }

  private void when() {
    if (lastWord != Word.GIVEN) {
      history.cut(events);
    }
    lastWord = Word.WHEN;
    events = history.get();
  }

  private void then() {
    lastWord = Word.THEN;
    events = history.get();
  }

  public void givenTest(Object test) {
    given();
    facade.givenTest(test);
  }

  public void given(Closure closure) {
    given();
    facade.given(closure);
  }

  public void given(VoidClosure closure) {
    given();
    facade.given(closure);
  }

  public <T> T given(T object) {
    given();
    return facade.given(object);
  }

  public void given(boolean primitive) {
    given();
    facade.given(primitive);
  }

  public void given(double primitive) {
    given();
    facade.given(primitive);
  }

  public <T> T givenTry(T object) {
    given();
    return facade.givenTry(object);
  }

  public void givenTimes(int number, Closure closure) {
    given();
    facade.givenTimes(number, closure);
  }

  public void givenTimes(int number, VoidClosure closure) {
    given();
    facade.givenTimes(number, closure);
  }

  public <T> T givenTimes(int number, T object) {
    given();
    return facade.givenTimes(number, object);
  }

  public <T> T mock(Class<T> type) {
    return facade.mock(type);
  }

  public <T> T spy(T real) {
    return facade.spy(real);
  }

  public <T> T given(Handler handler, T mock) {
    given();
    return facade.given(handler, mock);
  }

  public void given(Handler handler, InvocationMatcher invocationMatcher) {
    given();
    facade.given(handler, invocationMatcher);
  }

  public Handler willReturn(Object object) {
    return facade.willReturn(object);
  }

  public Handler willThrow(Throwable throwable) {
    return facade.willThrow(throwable);
  }

  public Handler willRethrow(Throwable throwable) {
    return facade.willRethrow(throwable);
  }

  public Handler willSpy(Object real) {
    return facade.willSpy(real);
  }

  public <T> T any(Class<T> type) {
    return facade.any(type);
  }

  public <T> T any(Class<T> type, Object matcher) {
    return facade.any(type, matcher);
  }

  public <T> T anyInstanceOf(Class<T> type) {
    return facade.anyInstanceOf(type);
  }

  public boolean a(boolean value) {
    return facade.a(value);
  }

  public char a(char value) {
    return facade.a(value);
  }

  public byte a(byte value) {
    return facade.a(value);
  }

  public short a(short value) {
    return facade.a(value);
  }

  public int a(int value) {
    return facade.a(value);
  }

  public long a(long value) {
    return facade.a(value);
  }

  public float a(float value) {
    return facade.a(value);
  }

  public double a(double value) {
    return facade.a(value);
  }

  public <T> T a(T value) {
    return facade.a(value);
  }

  public <T> T the(T value) {
    return facade.the(value);
  }

  public void the(boolean value) {
    facade.the(value);
  }

  public void the(double value) {
    facade.the(value);
  }

  public InvocationMatcher onInstance(Object mock) {
    return facade.onInstance(mock);
  }

  public InvocationMatcher onReturn(Class<?> type) {
    return facade.onReturn(type);
  }

  public InvocationMatcher onRequest(Class<?> type, Object... arguments) {
    return facade.onRequest(type, arguments);
  }

  public <T> T when(T object) {
    when();
    return facade.when(object);
  }

  public void when(Closure closure) {
    when();
    facade.when(closure);
  }

  public void when(VoidClosure closure) {
    when();
    facade.when(closure);
  }

  public void when(boolean value) {
    when();
    facade.when(value);
  }

  public void when(char value) {
    when();
    facade.when(value);
  }

  public void when(byte value) {
    when();
    facade.when(value);
  }

  public void when(short value) {
    when();
    facade.when(value);
  }

  public void when(int value) {
    when();
    facade.when(value);
  }

  public void when(long value) {
    when();
    facade.when(value);
  }

  public void when(float value) {
    when();
    facade.when(value);
  }

  public void when(double value) {
    when();
    facade.when(value);
  }

  public void thenReturned(Object objectOrMatcher) {
    then();
    facade.thenReturned(objectOrMatcher);
  }

  public void thenReturned(boolean value) {
    then();
    facade.thenReturned(value);
  }

  public void thenReturned(char value) {
    then();
    facade.thenReturned(value);
  }

  public void thenReturned(byte value) {
    then();
    facade.thenReturned(value);
  }

  public void thenReturned(short value) {
    then();
    facade.thenReturned(value);
  }

  public void thenReturned(int value) {
    then();
    facade.thenReturned(value);
  }

  public void thenReturned(long value) {
    then();
    facade.thenReturned(value);
  }

  public void thenReturned(float value) {
    then();
    facade.thenReturned(value);
  }

  public void thenReturned(double value) {
    then();
    facade.thenReturned(value);
  }

  public void thenReturned() {
    then();
    facade.thenReturned();
  }

  public void thenThrown(Object matcher) {
    then();
    facade.thenThrown(matcher);
  }

  public void thenThrown(Throwable throwable) {
    then();
    facade.thenThrown(throwable);
  }

  public void thenThrown(Class<? extends Throwable> type) {
    then();
    facade.thenThrown(type);
  }

  public void thenThrown() {
    then();
    facade.thenThrown();
  }

  public void then(boolean condition) {
    then();
    facade.then(condition);
  }

  public void then(Object object, Object matcher) {
    then();
    facade.then(object, matcher);
  }

  public void thenEqual(Object object, Object expected) {
    then();
    facade.thenEqual(object, expected);
  }

  public <T> T thenCalled(T mock) {
    then();
    return facade.thenCalled(mock);
  }

  public void thenCalled(InvocationMatcher invocationMatcher) {
    then();
    facade.thenCalled(invocationMatcher);
  }

  public <T> T thenCalledNever(T mock) {
    then();
    return facade.thenCalledNever(mock);
  }

  public void thenCalledNever(InvocationMatcher invocationMatcher) {
    then();
    facade.thenCalledNever(invocationMatcher);
  }

  public <T> T thenCalledTimes(int number, T mock) {
    then();
    return facade.thenCalledTimes(number, mock);
  }

  public void thenCalledTimes(int number, InvocationMatcher invocationMatcher) {
    then();
    facade.thenCalledTimes(number, invocationMatcher);
  }

  public <T> T thenCalledTimes(Object numberMatcher, T mock) {
    then();
    return facade.thenCalledTimes(numberMatcher, mock);
  }

  public void thenCalledTimes(Object numberMatcher, InvocationMatcher invocationMatcher) {
    then();
    facade.thenCalledTimes(numberMatcher, invocationMatcher);
  }

  public <T> T thenCalledInOrder(T mock) {
    then();
    return facade.thenCalledInOrder(mock);
  }

  public void thenCalledInOrder(InvocationMatcher invocationMatcher) {
    then();
    facade.thenCalledInOrder(invocationMatcher);
  }
}
