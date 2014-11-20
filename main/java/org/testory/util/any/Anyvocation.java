package org.testory.util.any;

import static org.testory.common.Checks.checkNotNull;
import static org.testory.common.Collections.immutable;
import static org.testory.common.Collections.last;

import java.lang.reflect.Method;
import java.util.List;

import org.testory.common.Any;

public class Anyvocation {
  public final Method method;
  public final Object instance;
  public final List<Object> arguments;
  public final List<Any> anys;

  private Anyvocation(Method method, Object instance, List<Object> arguments, List<Any> anys) {
    this.method = method;
    this.instance = instance;
    this.arguments = arguments;
    this.anys = anys;
  }

  public static Anyvocation anyvocation(Method method, Object instance, List<Object> arguments,
      List<Any> anys) {
    checkNotNull(method);
    checkNotNull(instance);
    checkNotNull(arguments);
    checkNotNull(anys);
    return new Anyvocation(method, instance, immutable(arguments), immutable(anys));
  }

  public static boolean isVarargs(Anyvocation anyvocation) {
    return anyvocation.method.isVarArgs() && !anyvocation.anys.isEmpty()
        && last(anyvocation.anys).token != last(anyvocation.arguments);
  }
}
