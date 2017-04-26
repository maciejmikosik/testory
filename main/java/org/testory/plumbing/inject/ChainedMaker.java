package org.testory.plumbing.inject;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.testory.plumbing.PlumbingException.check;

import java.util.ArrayList;
import java.util.List;

import org.testory.plumbing.Maker;

public class ChainedMaker implements Maker {
  private final List<Maker> makersList;

  private ChainedMaker(List<Maker> makersList) {
    this.makersList = makersList;
  }

  public static Maker chain(Maker... makers) {
    check(makers != null);
    List<Maker> makersList = new ArrayList<>(asList(makers));
    check(!makersList.contains(null));
    return new ChainedMaker(makersList);
  }

  public <T> T make(Class<T> type, String name) {
    check(type != null);
    check(name != null);
    for (Maker maker : makersList) {
      try {
        return maker.make(type, name);
      } catch (RuntimeException e) {}
    }
    throw new RuntimeException(format("cannot make %s of type %s", name, type.getName()));
  }
}
