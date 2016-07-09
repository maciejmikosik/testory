package org.testory;

import static org.testory.MockProxer.mockProxer;
import static org.testory.TestoryException.check;
import static org.testory.common.Matchers.isMatcher;
import static org.testory.plumbing.Formatter.formatter;
import static org.testory.plumbing.capture.AnySupport.anySupport;
import static org.testory.plumbing.capture.Repairer.repairer;
import static org.testory.plumbing.history.FilteredHistory.filter;
import static org.testory.plumbing.history.PurgedHistory.newPurgedHistory;
import static org.testory.plumbing.inject.ArrayMaker.singletonArray;
import static org.testory.plumbing.inject.ChainedMaker.chain;
import static org.testory.plumbing.inject.FinalMaker.finalMaker;
import static org.testory.plumbing.inject.PrimitiveMaker.randomPrimitiveMaker;
import static org.testory.plumbing.mock.NiceMockMaker.nice;
import static org.testory.plumbing.mock.RawMockMaker.rawMockMaker;
import static org.testory.plumbing.mock.SaneMockMaker.sane;
import static org.testory.plumbing.mock.UniqueNamer.uniqueNamer;

import org.testory.plumbing.Formatter;
import org.testory.plumbing.Maker;
import org.testory.plumbing.Mocking;
import org.testory.plumbing.capture.AnyException;
import org.testory.plumbing.capture.AnySupport;
import org.testory.plumbing.capture.Capturer;
import org.testory.plumbing.history.FilteredHistory;
import org.testory.plumbing.history.History;
import org.testory.plumbing.inject.Injector;
import org.testory.plumbing.mock.Namer;
import org.testory.proxy.CglibProxer;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;
import org.testory.proxy.Proxer;

public class Facade {
  public final Formatter formatter;
  public final History history;
  public final Proxer proxer;
  public final Namer mockNamer;
  public final Maker mockMaker;
  public final Injector injector;

  private final Capturer capturer;
  private final AnySupport anySupport;
  private final FilteredHistory<Mocking> mockingHistory;

  public Facade() {
    formatter = formatter();
    history = formatter.plug(newPurgedHistory());
    proxer = new CglibProxer();
    mockNamer = uniqueNamer(history);
    mockMaker = mockMaker(history, proxer);
    injector = injector(mockMaker);
    mockingHistory = filter(Mocking.class, history);
    anySupport = anySupport(history, repairer());
    capturer = anySupport.getCapturer();
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

  public boolean isMock(Object instance) {
    for (Mocking mocking : mockingHistory.get()) {
      if (mocking.mock == instance) {
        return true;
      }
    }
    return false;
  }

  public InvocationMatcher capture(Invocation invocation) {
    try {
      return capturer.capture(invocation);
    } catch (AnyException e) {
      throw new TestoryException(e);
    }
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
}
