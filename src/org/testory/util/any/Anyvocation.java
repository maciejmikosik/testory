package org.testory.util.any;

import static java.util.Collections.unmodifiableList;
import static org.testory.common.Checks.checkNotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
    return new Anyvocation(method, instance, unmodifiableList(new ArrayList<Object>(arguments)),
        unmodifiableList(new ArrayList<Any>(anys)));
  }
}
