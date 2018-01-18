package org.testory.facade;

import static org.testory.plumbing.PlumbingException.check;

import org.testory.common.Formatter;
import org.testory.plumbing.Checker;
import org.testory.plumbing.Maker;
import org.testory.plumbing.history.History;
import org.testory.plumbing.im.wildcard.WildcardSupport;
import org.testory.plumbing.inject.Injector;
import org.testory.plumbing.mock.Namer;
import org.testory.proxy.Proxer;

@SuppressWarnings("hiding")
public class Configuration {
  public final History history;
  public final Formatter formatter;
  public final Class<? extends RuntimeException> exception;
  public final Checker checker;
  public final Proxer proxer;
  public final Namer mockNamer;
  public final Maker mockMaker;
  public final Injector injector;
  public final WildcardSupport wildcardSupport;

  private Configuration(
      History history,
      Formatter formatter,
      Class<? extends RuntimeException> exception,
      Checker checker,
      Proxer proxer,
      Namer mockNamer,
      Maker mockMaker,
      Injector injector,
      WildcardSupport wildcardSupport) {
    this.history = history;
    this.formatter = formatter;
    this.exception = exception;
    this.checker = checker;
    this.proxer = proxer;
    this.mockNamer = mockNamer;
    this.mockMaker = mockMaker;
    this.injector = injector;
    this.wildcardSupport = wildcardSupport;
  }

  public static Configuration configuration() {
    return new Configuration(
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null);
  }

  public Configuration history(History history) {
    check(history != null);
    return new Configuration(
        history,
        formatter,
        exception,
        checker,
        proxer,
        mockNamer,
        mockMaker,
        injector,
        wildcardSupport);
  }

  public Configuration formatter(Formatter formatter) {
    check(formatter != null);
    return new Configuration(
        history,
        formatter,
        exception,
        checker,
        proxer,
        mockNamer,
        mockMaker,
        injector,
        wildcardSupport);
  }

  public Configuration exception(Class<? extends RuntimeException> exception) {
    check(exception != null);
    return new Configuration(
        history,
        formatter,
        exception,
        checker,
        proxer,
        mockNamer,
        mockMaker,
        injector,
        wildcardSupport);
  }

  public Configuration checker(Checker checker) {
    check(checker != null);
    return new Configuration(
        history,
        formatter,
        exception,
        checker,
        proxer,
        mockNamer,
        mockMaker,
        injector,
        wildcardSupport);
  }

  public Configuration proxer(Proxer proxer) {
    check(proxer != null);
    return new Configuration(
        history,
        formatter,
        exception,
        checker,
        proxer,
        mockNamer,
        mockMaker,
        injector,
        wildcardSupport);
  }

  public Configuration mockNamer(Namer mockNamer) {
    check(mockNamer != null);
    return new Configuration(
        history,
        formatter,
        exception,
        checker,
        proxer,
        mockNamer,
        mockMaker,
        injector,
        wildcardSupport);
  }

  public Configuration mockMaker(Maker mockMaker) {
    check(mockMaker != null);
    return new Configuration(
        history,
        formatter,
        exception,
        checker,
        proxer,
        mockNamer,
        mockMaker,
        injector,
        wildcardSupport);
  }

  public Configuration injector(Injector injector) {
    check(injector != null);
    return new Configuration(
        history,
        formatter,
        exception,
        checker,
        proxer,
        mockNamer,
        mockMaker,
        injector,
        wildcardSupport);
  }

  public Configuration wildcardSupport(WildcardSupport wildcardSupport) {
    check(wildcardSupport != null);
    return new Configuration(
        history,
        formatter,
        exception,
        checker,
        proxer,
        mockNamer,
        mockMaker,
        injector,
        wildcardSupport);
  }

  public Configuration validate() {
    check(history != null);
    check(formatter != null);
    check(exception != null);
    check(proxer != null);
    check(mockNamer != null);
    check(mockMaker != null);
    check(injector != null);
    check(wildcardSupport != null);
    return this;
  }
}
