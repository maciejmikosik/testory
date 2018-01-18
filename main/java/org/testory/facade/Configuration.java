package org.testory.facade;

import static java.util.Objects.requireNonNull;

import org.testory.common.Formatter;
import org.testory.plumbing.Checker;
import org.testory.plumbing.history.History;
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

  private Configuration(
      History history,
      Formatter formatter,
      Class<? extends RuntimeException> exception,
      Checker checker,
      Proxer proxer,
      Namer mockNamer) {
    this.history = history;
    this.formatter = formatter;
    this.exception = exception;
    this.checker = checker;
    this.proxer = proxer;
    this.mockNamer = mockNamer;
  }

  public static Configuration configuration() {
    return new Configuration(
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
        mockNamer);
  }

  public Configuration formatter(Formatter formatter) {
    return new Configuration(
        history,
        requireNonNull(formatter),
        exception,
        checker,
        proxer,
        mockNamer);
  }

  public Configuration exception(Class<? extends RuntimeException> exception) {
    return new Configuration(
        history,
        formatter,
        requireNonNull(exception),
        checker,
        proxer,
        mockNamer);
  }

  public Configuration checker(Checker checker) {
    return new Configuration(
        history,
        formatter,
        exception,
        requireNonNull(checker),
        proxer,
        mockNamer);
  }

  public Configuration proxer(Proxer proxer) {
    return new Configuration(
        history,
        formatter,
        exception,
        checker,
        requireNonNull(proxer),
        mockNamer);
  }

  public Configuration mockNamer(Namer mockNamer) {
    return new Configuration(
        history,
        formatter,
        exception,
        checker,
        proxer,
        requireNonNull(mockNamer));
  }

  public Configuration validate() {
    requireNonNull(history);
    requireNonNull(formatter);
    requireNonNull(exception);
    requireNonNull(proxer);
    requireNonNull(mockNamer);
    return this;
  }
}
