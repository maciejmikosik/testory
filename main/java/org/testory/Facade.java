package org.testory;

import static org.testory.common.Chain.chain;
import static org.testory.plumbing.inject.ArrayMaker.singletonArray;
import static org.testory.plumbing.inject.ChainedMaker.chain;
import static org.testory.plumbing.inject.FinalMaker.finalMaker;
import static org.testory.plumbing.inject.PrimitiveMaker.randomPrimitiveMaker;
import static org.testory.plumbing.mock.NiceMockMaker.nice;
import static org.testory.plumbing.mock.RawMockMaker.rawMockMaker;
import static org.testory.plumbing.mock.SaneMockMaker.sane;

import org.testory.common.Chain;
import org.testory.plumbing.Maker;
import org.testory.proxy.CglibProxer;
import org.testory.proxy.Proxer;

public class Facade {
  public final ThreadLocal<Chain<Object>> localHistory = new ThreadLocal<Chain<Object>>() {
    protected Chain<Object> initialValue() {
      return chain();
    }
  };

  public final Proxer proxer = new TestoryProxer(new CglibProxer());
  private final Maker rawMockMaker = rawMockMaker(proxer, localHistory);
  private final Maker niceMockMaker = nice(rawMockMaker, localHistory);
  public final Maker mockMaker = sane(niceMockMaker, localHistory);
  public final Maker fieldMaker = singletonArray(
      chain(randomPrimitiveMaker(), finalMaker(), mockMaker));

}
