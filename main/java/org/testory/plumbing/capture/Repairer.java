package org.testory.plumbing.capture;

import static java.util.Arrays.asList;
import static java.util.Collections.nCopies;
import static java.util.Objects.deepEquals;
import static org.testory.common.Collections.flip;
import static org.testory.common.Collections.last;
import static org.testory.plumbing.PlumbingException.check;
import static org.testory.plumbing.capture.Anyvocation.anyvocation;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Repairer {
  private Repairer() {}

  public static Repairer repairer() {
    return new Repairer();
  }

  public Anyvocation repair(Anyvocation anyvocation) {
    check(anyvocation != null);
    List<Class<?>> parameters = asList(anyvocation.method.getParameterTypes());
    boolean mayBeFolded = anyvocation.mayBeFolded();
    List<Object> unfolded = mayBeFolded
        ? unfoldArguments(anyvocation.arguments)
        : anyvocation.arguments;
    List<Class<?>> unfoldedParameters = mayBeFolded
        ? unfoldParameters(unfolded.size(), parameters)
        : parameters;
    List<Object> repairedUnfolded = repair(unfoldedParameters, unfolded, anyvocation);
    List<Object> repaired = mayBeFolded
        ? foldArguments(parameters.size(), repairedUnfolded)
        : repairedUnfolded;
    return anyvocation(anyvocation.method, anyvocation.instance, repaired, anyvocation.anys);
  }

  private static List<Object> repair(List<Class<?>> parameters, List<Object> arguments,
      Anyvocation anyvocation) {
    List<Boolean> solution = trySolveEager(anyvocation.anys, parameters, arguments);
    if (!deepEquals(flip(solution),
        trySolveEager(flip(anyvocation.anys), flip(parameters), flip(arguments)))) {
      throw new AnyException("found more than one solution");
    }
    List<CollectingAny> anysQueue = new ArrayList<CollectingAny>(anyvocation.anys);
    List<Object> repaired = new ArrayList<Object>();
    for (int i = 0; i < solution.size(); i++) {
      repaired.add(solution.get(i)
          ? anysQueue.remove(0).token
          : arguments.get(i));
    }
    return repaired;
  }

  private static List<Boolean> trySolveEager(List<CollectingAny> anys, List<Class<?>> parameters,
      List<Object> arguments) {
    List<Boolean> solution = new ArrayList<Boolean>(nCopies(arguments.size(), false));
    int nextIndex = 0;
    nextAny: for (CollectingAny any : anys) {
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
      throw new AnyException("cannot find any solution");
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
