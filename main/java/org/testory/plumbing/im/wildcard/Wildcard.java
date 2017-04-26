package org.testory.plumbing.im.wildcard;

import static org.testory.plumbing.PlumbingException.check;

import org.testory.common.Matcher;

public class Wildcard {
  public final Matcher matcher;
  public final Object token;

  private Wildcard(Matcher matcher, Object token) {
    this.matcher = matcher;
    this.token = token;
  }

  public static Wildcard wildcard(Matcher matcher, Object token) {
    check(matcher != null);
    check(token != null);
    return new Wildcard(matcher, token);
  }
}
