package org.testory;

import static org.testory.facade.Configuration.configuration;
import static org.testory.facade.DefaultFacade.defaultFacade;
import static org.testory.facade.PurgingFacade.purging;
import static org.testory.plumbing.Checker.checker;
import static org.testory.plumbing.CheckingProxer.checkingProxer;
import static org.testory.plumbing.format.MessageFormatter.messageFormatter;
import static org.testory.plumbing.format.QuietFormatter.quiet;
import static org.testory.plumbing.history.RawHistory.newRawHistory;
import static org.testory.plumbing.history.SynchronizedHistory.synchronize;
import static org.testory.plumbing.inject.ArrayMaker.singletonArray;
import static org.testory.plumbing.inject.ChainedMaker.chain;
import static org.testory.plumbing.inject.FinalMaker.finalMaker;
import static org.testory.plumbing.inject.Injector.injector;
import static org.testory.plumbing.inject.RandomPrimitiveMaker.randomPrimitiveMaker;
import static org.testory.plumbing.mock.NiceMockMaker.nice;
import static org.testory.plumbing.mock.RawMockMaker.rawMockMaker;
import static org.testory.plumbing.mock.SaneMockMaker.sane;
import static org.testory.plumbing.mock.UniqueNamer.uniqueNamer;
import static org.testory.proxy.proxer.CglibProxer.cglibProxer;
import static org.testory.proxy.proxer.FixObjectBugProxer.fixObjectBug;
import static org.testory.proxy.proxer.JdkCollectionsProxer.jdkCollections;
import static org.testory.proxy.proxer.NonFinalProxer.nonFinal;
import static org.testory.proxy.proxer.RepeatableProxy.repeatable;
import static org.testory.proxy.proxer.TypeSafeProxer.typeSafe;
import static org.testory.proxy.proxer.WrappingProxer.wrapping;

import org.testory.common.Closure;
import org.testory.common.Nullable;
import org.testory.common.VoidClosure;
import org.testory.facade.Configuration;
import org.testory.facade.Facade;
import org.testory.plumbing.Checker;
import org.testory.plumbing.Maker;
import org.testory.plumbing.format.QuietFormatter;
import org.testory.plumbing.history.History;
import org.testory.plumbing.inject.Injector;
import org.testory.plumbing.mock.Namer;
import org.testory.proxy.Handler;
import org.testory.proxy.InvocationMatcher;
import org.testory.proxy.Proxer;

public class Testory {
  private static final ThreadLocal<Facade> localFacade = new ThreadLocal<Facade>() {
    protected Facade initialValue() {
      Class<TestoryException> exception = TestoryException.class;
      History loudHistory = synchronize(newRawHistory());
      QuietFormatter formatter = quiet(messageFormatter());
      History history = formatter.quiet(loudHistory);
      Checker checker = checker(history, exception);
      Proxer proxer = wrapping(exception,
          nonFinal(typeSafe(jdkCollections(fixObjectBug(repeatable(
              wrapping(exception, cglibProxer())))))));
      Namer mockNamer = uniqueNamer(history);
      Maker mockMaker = sane(history, nice(history, rawMockMaker(history, checkingProxer(checker, proxer))));
      Injector injector = injector(singletonArray(chain(randomPrimitiveMaker(), finalMaker(), mockMaker)));
      Configuration configuration = configuration()
          .history(history)
          .formatter(formatter)
          .exception(exception)
          .checker(checker)
          .proxer(proxer)
          .mockNamer(mockNamer)
          .mockMaker(mockMaker)
          .injector(injector)
          .validate();
      return purging(history, cglibProxer(), defaultFacade(configuration));
    }
  };

  private static Facade getFacade() {
    return localFacade.get();
  }

  public static void givenTest(Object test) {
    getFacade().givenTest(test);
  }

  public static void given(Closure closure) {
    getFacade().given(closure);
  }

  public static void given(VoidClosure closure) {
    getFacade().given(closure);
  }

  public static <T> T given(T object) {
    return getFacade().given(object);
  }

  public static void given(boolean primitive) {
    getFacade().given(primitive);
  }

  public static void given(double primitive) {
    getFacade().given(primitive);
  }

  public static <T> T givenTry(T object) {
    return getFacade().givenTry(object);
  }

  public static void givenTimes(int number, Closure closure) {
    getFacade().givenTimes(number, closure);
  }

  public static void givenTimes(int number, VoidClosure closure) {
    getFacade().givenTimes(number, closure);
  }

  public static <T> T givenTimes(int number, T object) {
    return getFacade().givenTimes(number, object);
  }

  public static <T> T mock(Class<T> type) {
    return getFacade().mock(type);
  }

  public static <T> T spy(T real) {
    return getFacade().spy(real);
  }

  public static <T> T given(Handler handler, T mock) {
    return getFacade().given(handler, mock);
  }

  public static void given(Handler handler, InvocationMatcher invocationMatcher) {
    getFacade().given(handler, invocationMatcher);
  }

  public static Handler willReturn(@Nullable Object object) {
    return getFacade().willReturn(object);
  }

  public static Handler willThrow(Throwable throwable) {
    return getFacade().willThrow(throwable);
  }

  public static Handler willRethrow(Throwable throwable) {
    return getFacade().willRethrow(throwable);
  }

  public static Handler willSpy(Object real) {
    return getFacade().willSpy(real);
  }

  public static <T> T any(Class<T> type) {
    return getFacade().any(type);
  }

  public static <T> T any(Class<T> type, Object matcher) {
    return getFacade().any(type, matcher);
  }

  public static <T> T anyInstanceOf(Class<T> type) {
    return getFacade().anyInstanceOf(type);
  }

  public static boolean a(boolean value) {
    return getFacade().a(value);
  }

  public static char a(char value) {
    return getFacade().a(value);
  }

  public static byte a(byte value) {
    return getFacade().a(value);
  }

  public static short a(short value) {
    return getFacade().a(value);
  }

  public static int a(int value) {
    return getFacade().a(value);
  }

  public static long a(long value) {
    return getFacade().a(value);
  }

  public static float a(float value) {
    return getFacade().a(value);
  }

  public static double a(double value) {
    return getFacade().a(value);
  }

  public static <T> T a(T value) {
    return getFacade().a(value);
  }

  public static void the(boolean value) {
    getFacade().the(value);
  }

  public static void the(double value) {
    getFacade().the(value);
  }

  public static <T> T the(T instance) {
    return getFacade().the(instance);
  }

  public static InvocationMatcher onInstance(Object mock) {
    return getFacade().onInstance(mock);
  }

  public static InvocationMatcher onReturn(Class<?> type) {
    return getFacade().onReturn(type);
  }

  public static InvocationMatcher onRequest(Class<?> type, final Object... arguments) {
    return getFacade().onRequest(type, arguments);
  }

  public static <T> T when(T object) {
    return getFacade().when(object);
  }

  public static void when(Closure closure) {
    getFacade().when(closure);
  }

  public static void when(VoidClosure closure) {
    getFacade().when(closure);
  }

  public static void when(boolean value) {
    getFacade().when(value);
  }

  public static void when(char value) {
    getFacade().when(value);
  }

  public static void when(byte value) {
    getFacade().when(value);
  }

  public static void when(short value) {
    getFacade().when(value);
  }

  public static void when(int value) {
    getFacade().when(value);
  }

  public static void when(long value) {
    getFacade().when(value);
  }

  public static void when(float value) {
    getFacade().when(value);
  }

  public static void when(double value) {
    getFacade().when(value);
  }

  public static void thenReturned(@Nullable Object objectOrMatcher) {
    getFacade().thenReturned(objectOrMatcher);
  }

  public static void thenReturned(boolean value) {
    getFacade().thenReturned(value);
  }

  public static void thenReturned(char value) {
    getFacade().thenReturned(value);
  }

  public static void thenReturned(byte value) {
    getFacade().thenReturned(value);
  }

  public static void thenReturned(short value) {
    getFacade().thenReturned(value);
  }

  public static void thenReturned(int value) {
    getFacade().thenReturned(value);
  }

  public static void thenReturned(long value) {
    getFacade().thenReturned(value);
  }

  public static void thenReturned(float value) {
    getFacade().thenReturned(value);
  }

  public static void thenReturned(double value) {
    getFacade().thenReturned(value);
  }

  public static void thenReturned() {
    getFacade().thenReturned();
  }

  public static void thenThrown(Object matcher) {
    getFacade().thenThrown(matcher);
  }

  public static void thenThrown(Throwable throwable) {
    getFacade().thenThrown(throwable);
  }

  public static void thenThrown(Class<? extends Throwable> type) {
    getFacade().thenThrown(type);
  }

  public static void thenThrown() {
    getFacade().thenThrown();
  }

  public static void then(boolean condition) {
    getFacade().then(condition);
  }

  public static void then(@Nullable Object object, Object matcher) {
    getFacade().then(object, matcher);
  }

  public static void thenEqual(@Nullable Object object, @Nullable Object expected) {
    getFacade().thenEqual(object, expected);
  }

  public static <T> T thenCalled(T mock) {
    return getFacade().thenCalled(mock);
  }

  public static void thenCalled(InvocationMatcher invocationMatcher) {
    getFacade().thenCalled(invocationMatcher);
  }

  public static <T> T thenCalledNever(T mock) {
    return getFacade().thenCalledNever(mock);
  }

  public static void thenCalledNever(InvocationMatcher invocationMatcher) {
    getFacade().thenCalledNever(invocationMatcher);
  }

  public static <T> T thenCalledTimes(int number, T mock) {
    return getFacade().thenCalledTimes(number, mock);
  }

  public static void thenCalledTimes(int number, InvocationMatcher invocationMatcher) {
    getFacade().thenCalledTimes(number, invocationMatcher);
  }

  public static <T> T thenCalledTimes(Object numberMatcher, T mock) {
    return getFacade().thenCalledTimes(numberMatcher, mock);
  }

  public static void thenCalledTimes(Object numberMatcher, InvocationMatcher invocationMatcher) {
    getFacade().thenCalledTimes(numberMatcher, invocationMatcher);
  }

  public static <T> T thenCalledInOrder(T mock) {
    return getFacade().thenCalledInOrder(mock);
  }

  public static void thenCalledInOrder(InvocationMatcher invocationMatcher) {
    getFacade().thenCalledInOrder(invocationMatcher);
  }
}
