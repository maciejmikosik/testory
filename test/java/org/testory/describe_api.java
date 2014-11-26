package org.testory;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.testory.common.Nullable;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class describe_api {
  @Test
  public void api_exposes_only_required_types() {
    HashSet<Class<?>> api = new HashSet<Class<?>>(asList(Testory.class, Closure.class,
        Invocation.class, Handler.class, InvocationMatcher.class, Nullable.class));
    assertEquals(api, filterNonJdk(allDependencies(Testory.class)));
  }

  private static Set<Class<?>> allDependencies(Class<?> type) {
    Set<Class<?>> dependencies = new HashSet<Class<?>>();
    Set<Class<?>> remaining = new HashSet<Class<?>>();
    remaining.add(type);

    while (!remaining.isEmpty()) {
      Class<?> current = remaining.iterator().next();
      dependencies.add(current);
      remaining.addAll(directDependencies(current));
      remaining.removeAll(dependencies);
    }
    return dependencies;
  }

  private static Set<Class<?>> directDependencies(Class<?> type) {
    Set<Class<?>> dependencies = new HashSet<Class<?>>();
    for (Method method : type.getMethods()) {
      dependencies.add(method.getReturnType());
      dependencies.addAll(asList(method.getParameterTypes()));
      for (Annotation annotation : method.getAnnotations()) {
        dependencies.add(annotation.annotationType());
      }
      for (Annotation[] parameter : method.getParameterAnnotations()) {
        for (Annotation annotation : parameter) {
          dependencies.add(annotation.annotationType());
        }
      }
    }
    if (type.isArray()) {
      dependencies.add(type.getComponentType());
    }
    return dependencies;
  }

  private static Set<Class<?>> filterNonJdk(Set<Class<?>> types) {
    Set<Class<?>> filtered = new HashSet<Class<?>>();
    for (Class<?> type : types) {
      if (!type.isPrimitive() && !type.isArray()
          && !type.getPackage().getName().startsWith("java.")
          && !type.getPackage().getName().startsWith("sun.")) {
        filtered.add(type);
      }
    }
    return filtered;
  }
}
