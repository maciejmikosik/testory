package org.testory.plumbing.im.wildcard;

import static org.testory.common.Collections.immutable;
import static org.testory.common.Collections.last;
import static org.testory.plumbing.PlumbingException.check;

import java.lang.reflect.Method;
import java.util.List;

public class WildcardInvocation {
  public final Method method;
  public final Object instance;
  public final List<Object> arguments;
  public final List<Wildcard> wildcards;

  private WildcardInvocation(
      Method method,
      Object instance,
      List<Object> arguments,
      List<Wildcard> wildcards) {
    this.method = method;
    this.instance = instance;
    this.arguments = arguments;
    this.wildcards = wildcards;
  }

  public static WildcardInvocation wildcardInvocation(
      Method method,
      Object instance,
      List<Object> arguments,
      List<Wildcard> wildcards) {
    check(method != null);
    check(instance != null);
    check(arguments != null);
    check(wildcards != null);
    return new WildcardInvocation(method, instance, immutable(arguments), immutable(wildcards));
  }

  /** may there be any() inside varargs array */
  public boolean mayBeFolded() {
    return method.isVarArgs()
        && !wildcards.isEmpty()
        && last(wildcards).token != last(arguments);
  }
}
