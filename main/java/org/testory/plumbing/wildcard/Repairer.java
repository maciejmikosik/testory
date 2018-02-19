package org.testory.plumbing.wildcard;

import static java.util.Arrays.asList;
import static java.util.Collections.nCopies;
import static java.util.Objects.deepEquals;
import static org.testory.common.Collections.flip;
import static org.testory.common.Collections.last;
import static org.testory.plumbing.PlumbingException.check;
import static org.testory.plumbing.wildcard.WildcardInvocation.wildcardInvocation;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.testory.plumbing.Checker;

public class Repairer {
  private final Checker checker;

  private Repairer(Checker checker) {
    this.checker = checker;
  }

  public static Repairer repairer(Checker checker) {
    check(checker != null);
    return new Repairer(checker);
  }

  public WildcardInvocation repair(WildcardInvocation invocation) {
    check(invocation != null);
    List<Class<?>> parameters = asList(invocation.method.getParameterTypes());
    boolean mayBeFolded = invocation.mayBeFolded();
    List<Object> unfolded = mayBeFolded
        ? unfoldArguments(invocation.arguments)
        : invocation.arguments;
    List<Class<?>> unfoldedParameters = mayBeFolded
        ? unfoldParameters(unfolded.size(), parameters)
        : parameters;
    List<Object> repairedUnfolded = repair(unfoldedParameters, unfolded, invocation);
    List<Object> repaired = mayBeFolded
        ? foldArguments(parameters.size(), repairedUnfolded)
        : repairedUnfolded;
    return wildcardInvocation(
        invocation.method,
        invocation.instance,
        repaired,
        invocation.wildcards);
  }

  private List<Object> repair(
      List<Class<?>> parameters,
      List<Object> arguments,
      WildcardInvocation invocation) {
    List<Boolean> solution = trySolveEager(invocation.wildcards, parameters, arguments);
    if (!deepEquals(
        flip(solution),
        trySolveEager(flip(invocation.wildcards), flip(parameters), flip(arguments)))) {
      checker.fail("found more than one solution");
    }
    List<Wildcard> wildcards = new ArrayList<>(invocation.wildcards);
    List<Object> repaired = new ArrayList<>();
    for (int i = 0; i < solution.size(); i++) {
      repaired.add(solution.get(i)
          ? wildcards.remove(0).token
          : arguments.get(i));
    }
    return repaired;
  }

  private List<Boolean> trySolveEager(
      List<Wildcard> wildcards,
      List<Class<?>> parameters,
      List<Object> arguments) {
    List<Boolean> solution = new ArrayList<>(nCopies(arguments.size(), false));
    int nextIndex = 0;
    nextWildcard: for (Wildcard wildcard : wildcards) {
      for (int i = nextIndex; i < arguments.size(); i++) {
        if (wildcard.token == arguments.get(i)) {
          solution.set(i, true);
          nextIndex = i + 1;
          continue nextWildcard;
        }
      }
      for (int i = nextIndex; i < arguments.size(); i++) {
        if (parameters.get(i).isPrimitive()) {
          solution.set(i, true);
          nextIndex = i + 1;
          continue nextWildcard;
        }
      }
      checker.fail("cannot find any solution");
    }
    return solution;
  }

  private static List<Class<?>> unfoldParameters(int size, List<Class<?>> parameters) {
    List<Class<?>> unfolded = new ArrayList<>();
    unfolded.addAll(parameters.subList(0, parameters.size() - 1));
    unfolded.addAll(nCopies(size - (parameters.size() - 1), last(parameters).getComponentType()));
    return unfolded;
  }

  private static List<Object> unfoldArguments(List<?> folded) {
    ArrayList<Object> unfolded = new ArrayList<>();
    unfolded.addAll(folded.subList(0, folded.size() - 1));
    unfolded.addAll(asBoxingList(last(folded)));
    return unfolded;
  }

  private static List<Object> asBoxingList(Object array) {
    List<Object> list = new ArrayList<>();
    for (int i = 0; i < Array.getLength(array); i++) {
      list.add(Array.get(array, i));
    }
    return list;
  }

  private static List<Object> foldArguments(int length, List<Object> arguments) {
    List<Object> folded = new ArrayList<>();
    folded.addAll(arguments.subList(0, length - 1));
    folded.add(asArray(arguments.subList(length - 1, arguments.size())));
    return folded;
  }

  private static Object asArray(List<Object> elements) {
    Object array = Array.newInstance(Object.class, 0);
    return elements.toArray((Object[]) array);
  }
}
