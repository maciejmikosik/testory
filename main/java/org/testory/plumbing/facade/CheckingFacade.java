package org.testory.plumbing.facade;

import static java.util.Arrays.asList;
import static org.testory.plumbing.PlumbingException.check;
import static org.testory.proxy.Invocation.invocation;
import static org.testory.proxy.Typing.implementing;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import org.testory.common.Named;
import org.testory.common.Nullable;
import org.testory.common.Optional;
import org.testory.plumbing.Checker;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.Proxer;

public class CheckingFacade {
  public static Facade checking(final Checker checker, Proxer proxer, final Facade facade) {
    check(checker != null);
    check(proxer != null);
    check(facade != null);
    return (Facade) proxer.proxy(implementing(Facade.class), new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        Method method = invocation.method;
        String methodName = method.getName();
        Class<?>[] parameters = method.getParameterTypes();
        Annotation[][] parametersAnnotations = method.getParameterAnnotations();

        for (int iParameter = 0; iParameter < parameters.length; iParameter++) {
          Class<?> parameter = parameters[iParameter];
          Object argument = invocation.arguments.get(iParameter);
          List<Annotation> annotations = asList(parametersAnnotations[iParameter]);

          if (!find(Nullable.class, annotations).isPresent()) {
            checker.notNull(argument);
          }
          if (methodName.startsWith("thenReturned")
              || methodName.startsWith("thenThrown")) {
            checker.mustCallWhen();
          }

          Optional<Named> namedAnnotation = find(Named.class, annotations);
          if (namedAnnotation.isPresent()) {
            String name = namedAnnotation.get().value();
            if (name.toLowerCase().contains("mock")) {
              checker.mock(argument);
            }
            if (name.toLowerCase().contains("matcher")) {
              checker.matcher(argument);
            }
          }

          if (methodName.endsWith("Times")
              && iParameter == 0
              && parameter == int.class) {
            checker.notNegative((Integer) argument);
          }
        }
        return invocation(method, facade, invocation.arguments).invoke();
      }
    });
  }

  private static <A extends Annotation> Optional<A> find(Class<A> type, List<Annotation> annotations) {
    for (Annotation annotation : annotations) {
      if (annotation.annotationType() == type) {
        return (Optional<A>) Optional.of(annotation);
      }
    }
    return Optional.empty();
  }
}
