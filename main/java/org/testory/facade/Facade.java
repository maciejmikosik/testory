package org.testory.facade;

import org.testory.common.Closure;
import org.testory.common.VoidClosure;
import org.testory.proxy.Handler;
import org.testory.proxy.InvocationMatcher;

public interface Facade {
  void givenTest(Object test);

  void given(Closure closure);

  <T> T given(T object);

  void given(boolean primitive);

  void given(double primitive);

  <T> T givenTry(T object);

  void givenTimes(int number, Closure closure);

  <T> T givenTimes(int number, T object);

  <T> T mock(Class<T> type);

  <T> T spy(T real);

  <T> T given(Handler handler, T mock);

  void given(Handler handler, InvocationMatcher invocationMatcher);

  Handler willReturn(Object object);

  Handler willThrow(Throwable throwable);

  Handler willRethrow(Throwable throwable);

  Handler willSpy(Object real);

  <T> T any(Class<T> type);

  <T> T any(Class<T> type, Object matcher);

  boolean a(boolean value);

  char a(char value);

  byte a(byte value);

  short a(short value);

  int a(int value);

  long a(long value);

  float a(float value);

  double a(double value);

  <T> T a(T value);

  <T> T the(T value);

  void the(boolean value);

  void the(double value);

  InvocationMatcher onInstance(Object mock);

  InvocationMatcher onReturn(Class<?> type);

  InvocationMatcher onRequest(Class<?> type, Object... arguments);

  <T> T when(T object);

  void when(Closure closure);

  void when(VoidClosure closure);

  void when(boolean value);

  void when(char value);

  void when(byte value);

  void when(short value);

  void when(int value);

  void when(long value);

  void when(float value);

  void when(double value);

  void thenReturned(Object objectOrMatcher);

  void thenReturned(boolean value);

  void thenReturned(char value);

  void thenReturned(byte value);

  void thenReturned(short value);

  void thenReturned(int value);

  void thenReturned(long value);

  void thenReturned(float value);

  void thenReturned(double value);

  void thenReturned();

  void thenThrown(Object matcher);

  void thenThrown(Throwable throwable);

  void thenThrown(Class<? extends Throwable> type);

  void thenThrown();

  void then(boolean condition);

  void then(Object object, Object matcher);

  void thenEqual(Object object, Object expected);

  <T> T thenCalled(T mock);

  void thenCalled(InvocationMatcher invocationMatcher);

  <T> T thenCalledNever(T mock);

  void thenCalledNever(InvocationMatcher invocationMatcher);

  <T> T thenCalledTimes(int number, T mock);

  void thenCalledTimes(int number, InvocationMatcher invocationMatcher);

  <T> T thenCalledTimes(Object numberMatcher, T mock);

  void thenCalledTimes(Object numberMatcher, InvocationMatcher invocationMatcher);

  <T> T thenCalledInOrder(T mock);

  void thenCalledInOrder(InvocationMatcher invocationMatcher);

}
