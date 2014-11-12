package org.testory.util.any;

import static java.util.Arrays.asList;
import static java.util.Collections.nCopies;
import static org.testory.common.Checks.checkNotNull;
import static org.testory.common.Collections.flip;
import static org.testory.common.Collections.last;
import static org.testory.common.Objects.areEqualDeep;
import static org.testory.util.any.Anyvocation.anyvocation;
import static org.testory.util.any.Anyvocation.isVarargs;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.testory.common.Nullable;
import org.testory.common.Optional;

public class Repairs {
  public static Optional<Anyvocation> repair(Anyvocation anyvocation) {
    checkNotNull(anyvocation);
    boolean isVarargs = isVarargs(anyvocation);
    List<Class<?>> parameters = asList(anyvocation.method.getParameterTypes());
    List<Object> unfolded = isVarargs
        ? unfoldArguments(anyvocation.arguments)
        : anyvocation.arguments;
    List<Class<?>> unfoldedParameters = isVarargs
        ? unfoldParameters(unfolded.size(), parameters)
        : parameters;
    List<Object> repairedUnfolded = repair(unfoldedParameters, unfolded, anyvocation);
    if (repairedUnfolded == null) {
      return Optional.empty();
    }
    List<Object> repaired = isVarargs
        ? foldArguments(parameters.size(), repairedUnfolded)
        : repairedUnfolded;
    return Optional.of(anyvocation(anyvocation.method, anyvocation.instance, repaired,
        anyvocation.anys));
  }

  @Nullable
  private static List<Object> repair(List<Class<?>> parameters, List<Object> arguments,
      Anyvocation anyvocation) {
    List<Boolean> solution = trySolveEager(anyvocation.anys, parameters, arguments);
    if (solution == null) {
      return null;
    }
    if (!areEqualDeep(flip(solution),
        trySolveEager(flip(anyvocation.anys), flip(parameters), flip(arguments)))) {
      return null;
    }
    List<Any> anysQueue = new ArrayList<Any>(anyvocation.anys);
    List<Object> repaired = new ArrayList<Object>();
    for (int i = 0; i < solution.size(); i++) {
      repaired.add(solution.get(i)
          ? anysQueue.remove(0).token
          : arguments.get(i));
    }
    return repaired;
  }

  @Nullable
  private static List<Boolean> trySolveEager(List<Any> anys, List<Class<?>> parameters,
      List<Object> arguments) {
    List<Boolean> solution = new ArrayList<Boolean>(nCopies(arguments.size(), false));
    int nextIndex = 0;
    nextAny: for (Any any : anys) {
      for (int i = nextIndex; i < arguments.size(); i++) {
        if (any.token == arguments.get(i)) {
          solution.set(i, true);
          nextIndex = i + 1;
          continue nextAny;
        }
      }
      for (int i = nextIndex; i < arguments.size(); i++) {
        if (parameters.get(i).isPrimitive()) {
          solution.set(i, true);
          nextIndex = i + 1;
          continue nextAny;
        }
      }
      return null;
    }
    return solution;
  }

  private static List<Class<?>> unfoldParameters(int size, List<Class<?>> parameters) {
    List<Class<?>> unfolded = new ArrayList<Class<?>>();
    unfolded.addAll(parameters.subList(0, parameters.size() - 1));
    unfolded.addAll(nCopies(size - (parameters.size() - 1), last(parameters).getComponentType()));
    return unfolded;
  }

  private static List<Object> unfoldArguments(List<?> folded) {
    ArrayList<Object> unfolded = new ArrayList<Object>();
    unfolded.addAll(folded.subList(0, folded.size() - 1));
    unfolded.addAll(asBoxingList(last(folded)));
    return unfolded;
  }

  private static List<Object> asBoxingList(Object array) {
    List<Object> list = new ArrayList<Object>();
    for (int i = 0; i < Array.getLength(array); i++) {
      list.add(Array.get(array, i));
    }
    return list;
  }

  private static List<Object> foldArguments(int length, List<Object> arguments) {
    List<Object> folded = new ArrayList<Object>();
    folded.addAll(arguments.subList(0, length - 1));
    folded.add(asArray(arguments.subList(length - 1, arguments.size())));
    return folded;
  }

  private static Object asArray(List<Object> elements) {
    Object array = Array.newInstance(Object.class, 0);
    return elements.toArray((Object[]) array);
  }
}
