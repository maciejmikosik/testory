package org.testory.plumbing.capture;

import static org.testory.plumbing.PlumbingException.check;

import org.testory.common.Matcher;

public class CollectingAny {
  public final Matcher matcher;
  public final Object token;

  private CollectingAny(Matcher matcher, Object token) {
    this.matcher = matcher;
    this.token = token;
  }

  public static CollectingAny collectingAny(Matcher matcher, Object token) {
    check(matcher != null);
    check(token != null);
    return new CollectingAny(matcher, token);
  }
}
