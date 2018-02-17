package org.testory.plumbing.facade;

import static org.testory.plumbing.PlumbingException.check;

import org.testory.common.PageFormatter;
import org.testory.plumbing.Checker;
import org.testory.plumbing.Maker;
import org.testory.plumbing.history.History;
import org.testory.plumbing.im.wildcard.WildcardSupport;
import org.testory.plumbing.inject.Injector;
import org.testory.plumbing.mock.Namer;
import org.testory.proxy.extra.Overrider;

@SuppressWarnings("hiding")
public class Configuration {
  public final History history;
  public final Checker checker;
  public final PageFormatter pageFormatter;
  public final Class<? extends RuntimeException> exception;
  public final Overrider overrider;
  public final Namer mockNamer;
  public final Maker mockMaker;
  public final Injector injector;
  public final WildcardSupport wildcardSupport;

  private Configuration(
      History history,
      Checker checker,
      PageFormatter pageFormatter,
      Class<? extends RuntimeException> exception,
      Overrider overrider,
      Namer mockNamer,
      Maker mockMaker,
      Injector injector,
      WildcardSupport wildcardSupport) {
    this.history = history;
    this.checker = checker;
    this.pageFormatter = pageFormatter;
    this.exception = exception;
    this.overrider = overrider;
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
        checker,
        pageFormatter,
        exception,
        overrider,
        mockNamer,
        mockMaker,
        injector,
        wildcardSupport);
  }

  public Configuration checker(Checker checker) {
    check(checker != null);
    return new Configuration(
        history,
        checker,
        pageFormatter,
        exception,
        overrider,
        mockNamer,
        mockMaker,
        injector,
        wildcardSupport);
  }

  public Configuration pageFormatter(PageFormatter pageFormatter) {
    check(pageFormatter != null);
    return new Configuration(
        history,
        checker,
        pageFormatter,
        exception,
        overrider,
        mockNamer,
        mockMaker,
        injector,
        wildcardSupport);
  }

  public Configuration exception(Class<? extends RuntimeException> exception) {
    check(exception != null);
    return new Configuration(
        history,
        checker,
        pageFormatter,
        exception,
        overrider,
        mockNamer,
        mockMaker,
        injector,
        wildcardSupport);
  }

  public Configuration overrider(Overrider overrider) {
    check(overrider != null);
    return new Configuration(
        history,
        checker,
        pageFormatter,
        exception,
        overrider,
        mockNamer,
        mockMaker,
        injector,
        wildcardSupport);
  }

  public Configuration mockNamer(Namer mockNamer) {
    check(mockNamer != null);
    return new Configuration(
        history,
        checker,
        pageFormatter,
        exception,
        overrider,
        mockNamer,
        mockMaker,
        injector,
        wildcardSupport);
  }

  public Configuration mockMaker(Maker mockMaker) {
    check(mockMaker != null);
    return new Configuration(
        history,
        checker,
        pageFormatter,
        exception,
        overrider,
        mockNamer,
        mockMaker,
        injector,
        wildcardSupport);
  }

  public Configuration injector(Injector injector) {
    check(injector != null);
    return new Configuration(
        history,
        checker,
        pageFormatter,
        exception,
        overrider,
        mockNamer,
        mockMaker,
        injector,
        wildcardSupport);
  }

  public Configuration wildcardSupport(WildcardSupport wildcardSupport) {
    check(wildcardSupport != null);
    return new Configuration(
        history,
        checker,
        pageFormatter,
        exception,
        overrider,
        mockNamer,
        mockMaker,
        injector,
        wildcardSupport);
  }

  public Configuration validate() {
    check(history != null);
    check(checker != null);
    check(pageFormatter != null);
    check(exception != null);
    check(overrider != null);
    check(mockNamer != null);
    check(mockMaker != null);
    check(injector != null);
    check(wildcardSupport != null);
    return this;
  }
}
