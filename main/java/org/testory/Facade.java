package org.testory;

import static org.testory.plumbing.inject.ArrayMaker.singletonArray;
import static org.testory.plumbing.inject.ChainedMaker.chain;
import static org.testory.plumbing.inject.FinalMaker.finalMaker;
import static org.testory.plumbing.inject.PrimitiveMaker.randomPrimitiveMaker;
import static org.testory.plumbing.mock.NiceMockMaker.nice;
import static org.testory.plumbing.mock.RawMockMaker.rawMockMaker;
import static org.testory.plumbing.mock.SaneMockMaker.sane;

import org.testory.plumbing.History;
import org.testory.plumbing.Maker;
import org.testory.proxy.CglibProxer;
import org.testory.proxy.Proxer;

public class Facade {
  public final History history = new History();
  public final Proxer proxer = new TestoryProxer(new CglibProxer());
  private final Maker rawMockMaker = rawMockMaker(proxer, history);
  private final Maker niceMockMaker = nice(rawMockMaker, history);
  public final Maker mockMaker = sane(niceMockMaker, history);
  public final Maker fieldMaker = singletonArray(
      chain(randomPrimitiveMaker(), finalMaker(), mockMaker));

}
