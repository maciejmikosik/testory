package org.testory.facade;

import static java.util.Objects.requireNonNull;

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
    return new Configuration(
        requireNonNull(history),
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
    return new Configuration(
        history,
        requireNonNull(formatter),
        exception,
        checker,
        proxer,
        mockNamer,
        mockMaker,
        injector,
        wildcardSupport);
  }

  public Configuration exception(Class<? extends RuntimeException> exception) {
    return new Configuration(
        history,
        formatter,
        requireNonNull(exception),
        checker,
        proxer,
        mockNamer,
        mockMaker,
        injector,
        wildcardSupport);
  }

  public Configuration checker(Checker checker) {
    return new Configuration(
        history,
        formatter,
        exception,
        requireNonNull(checker),
        proxer,
        mockNamer,
        mockMaker,
        injector,
        wildcardSupport);
  }

  public Configuration proxer(Proxer proxer) {
    return new Configuration(
        history,
        formatter,
        exception,
        checker,
        requireNonNull(proxer),
        mockNamer,
        mockMaker,
        injector,
        wildcardSupport);
  }

  public Configuration mockNamer(Namer mockNamer) {
    return new Configuration(
        history,
        formatter,
        exception,
        checker,
        proxer,
        requireNonNull(mockNamer),
        mockMaker,
        injector,
        wildcardSupport);
  }

  public Configuration mockMaker(Maker mockMaker) {
    return new Configuration(
        history,
        formatter,
        exception,
        checker,
        proxer,
        mockNamer,
        requireNonNull(mockMaker),
        injector,
        wildcardSupport);
  }

  public Configuration injector(Injector injector) {
    return new Configuration(
        history,
        formatter,
        exception,
        checker,
        proxer,
        mockNamer,
        mockMaker,
        requireNonNull(injector),
        wildcardSupport);
  }

  public Configuration wildcardSupport(WildcardSupport wildcardSupport) {
    return new Configuration(
        history,
        formatter,
        exception,
        checker,
        proxer,
        mockNamer,
        mockMaker,
        injector,
        requireNonNull(wildcardSupport));
  }

  public Configuration validate() {
    requireNonNull(history);
    requireNonNull(formatter);
    requireNonNull(exception);
    requireNonNull(proxer);
    requireNonNull(mockNamer);
    requireNonNull(mockMaker);
    requireNonNull(injector);
    requireNonNull(wildcardSupport);
    return this;
  }
}
