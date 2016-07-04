package org.testory;

import static org.testory.MockProxer.mockProxer;
import static org.testory.plumbing.FilteredHistory.filter;
import static org.testory.plumbing.inject.ArrayMaker.singletonArray;
import static org.testory.plumbing.inject.ChainedMaker.chain;
import static org.testory.plumbing.inject.FinalMaker.finalMaker;
import static org.testory.plumbing.inject.PrimitiveMaker.randomPrimitiveMaker;
import static org.testory.plumbing.mock.NiceMockMaker.nice;
import static org.testory.plumbing.mock.RawMockMaker.rawMockMaker;
import static org.testory.plumbing.mock.SaneMockMaker.sane;
import static org.testory.plumbing.mock.UniqueNamer.uniqueNamer;

import org.testory.plumbing.FilteredHistory;
import org.testory.plumbing.History;
import org.testory.plumbing.Maker;
import org.testory.plumbing.Mocking;
import org.testory.plumbing.inject.Injector;
import org.testory.plumbing.mock.Namer;
import org.testory.proxy.CglibProxer;
import org.testory.proxy.Proxer;

public class Facade {
  public final History history = new History();
  public final Proxer proxer = new CglibProxer();
  private final Proxer mockProxer = mockProxer(history, proxer);
  private final Maker rawMockMaker = rawMockMaker(mockProxer, history);
  private final Maker niceMockMaker = nice(rawMockMaker, history);
  public final Namer mockNamer = uniqueNamer(history);
  public final Maker mockMaker = sane(niceMockMaker, history);
  public final Injector injector = new Injector(singletonArray(
      chain(randomPrimitiveMaker(), finalMaker(), mockMaker)));

  private final FilteredHistory<Mocking> mockingHistory = filter(Mocking.class, history);

  public boolean isMock(Object instance) {
    for (Mocking mocking : mockingHistory.get()) {
      if (mocking.mock == instance) {
        return true;
      }
    }
    return false;
  }
}
