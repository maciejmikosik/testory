package org.testory.plumbing.capture;

import static org.testory.common.Collections.immutable;
import static org.testory.common.Collections.last;
import static org.testory.plumbing.PlumbingException.check;

import java.lang.reflect.Method;
import java.util.List;

public class Anyvocation {
  public final Method method;
  public final Object instance;
  public final List<Object> arguments;
  public final List<CollectingAny> anys;

  private Anyvocation(Method method, Object instance, List<Object> arguments, List<CollectingAny> anys) {
    this.method = method;
    this.instance = instance;
    this.arguments = arguments;
    this.anys = anys;
  }

  public static Anyvocation anyvocation(Method method, Object instance, List<Object> arguments,
      List<CollectingAny> anys) {
    check(method != null);
    check(instance != null);
    check(arguments != null);
    check(anys != null);
    return new Anyvocation(method, instance, immutable(arguments), immutable(anys));
  }

  /** may there be any() inside varargs array */
  public boolean mayBeFolded() {
    return method.isVarArgs()
        && !anys.isEmpty()
        && last(anys).token != last(arguments);
  }
}
