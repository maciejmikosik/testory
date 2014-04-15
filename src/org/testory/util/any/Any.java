package org.testory.util.any;

import static org.testory.common.Checks.checkNotNull;
import static org.testory.common.Classes.tryWrap;
import static org.testory.util.Uniques.unique;

import org.testory.common.Matcher;

public class Any {
  public final Class<?> type;
  public final Matcher matcher;
  public final Object token;

  private Any(Class<?> type, Matcher matcher, Object token) {
    this.type = type;
    this.matcher = matcher;
    this.token = token;
  }

  public static Any any(Class<?> type, Matcher matcher) {
    checkNotNull(type);
    checkNotNull(matcher);
    return new Any(type, matcher, unique(tryWrap(type)));
  }
}
