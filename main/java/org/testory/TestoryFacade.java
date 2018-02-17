package org.testory;

import static org.testory.common.PageFormatter.pageFormatter;
import static org.testory.plumbing.Checker.checker;
import static org.testory.plumbing.CheckingProxer.checkingProxer;
import static org.testory.plumbing.facade.CheckingFacade.checking;
import static org.testory.plumbing.facade.ConfigurableFacade.configurableFacade;
import static org.testory.plumbing.facade.Configuration.configuration;
import static org.testory.plumbing.facade.PurgingFacade.purging;
import static org.testory.plumbing.format.MessageFormatter.messageFormatter;
import static org.testory.plumbing.format.QuietFormatter.quiet;
import static org.testory.plumbing.history.RawHistory.newRawHistory;
import static org.testory.plumbing.history.SynchronizedHistory.synchronize;
import static org.testory.plumbing.im.wildcard.Repairer.repairer;
import static org.testory.plumbing.im.wildcard.Tokenizer.tokenizer;
import static org.testory.plumbing.im.wildcard.WildcardMatcherizer.wildcardMatcherizer;
import static org.testory.plumbing.im.wildcard.WildcardSupport.wildcardSupport;
import static org.testory.plumbing.inject.ArrayMaker.singletonArray;
import static org.testory.plumbing.inject.ChainedMaker.chain;
import static org.testory.plumbing.inject.FinalMaker.finalMaker;
import static org.testory.plumbing.inject.Injector.injector;
import static org.testory.plumbing.inject.RandomPrimitiveMaker.randomPrimitiveMaker;
import static org.testory.plumbing.mock.NiceMockMaker.nice;
import static org.testory.plumbing.mock.RawMockMaker.rawMockMaker;
import static org.testory.plumbing.mock.SaneMockMaker.sane;
import static org.testory.plumbing.mock.UniqueNamer.uniqueNamer;
import static org.testory.plumbing.verify.Verifier.verifier;
import static org.testory.proxy.extra.Overrider.overrider;
import static org.testory.proxy.proxer.CglibProxer.cglibProxer;
import static org.testory.proxy.proxer.FixObjectBugProxer.fixObjectBug;
import static org.testory.proxy.proxer.JdkCollectionsProxer.jdkCollections;
import static org.testory.proxy.proxer.NonFinalProxer.nonFinal;
import static org.testory.proxy.proxer.RepeatableProxer.repeatable;
import static org.testory.proxy.proxer.TypeSafeProxer.typeSafe;

import org.testory.common.PageFormatter;
import org.testory.plumbing.Checker;
import org.testory.plumbing.Maker;
import org.testory.plumbing.facade.Configuration;
import org.testory.plumbing.facade.Facade;
import org.testory.plumbing.format.QuietFormatter;
import org.testory.plumbing.history.History;
import org.testory.plumbing.im.wildcard.WildcardSupport;
import org.testory.proxy.Proxer;
import org.testory.proxy.extra.Overrider;

public class TestoryFacade {
  public static Facade testoryFacade() {
    Class<TestoryException> exception = TestoryException.class;
    QuietFormatter formatter = quiet(messageFormatter());
    PageFormatter pageFormatter = pageFormatter(formatter).add("\n");
    History history = formatter.quiet(synchronize(newRawHistory()));
    Checker checker = checker(history, exception);
    Proxer proxer = nonFinal(typeSafe(jdkCollections(fixObjectBug(repeatable(cglibProxer())))));
    Overrider overrider = overrider(proxer);
    Maker mockMaker = sane(history, nice(history, rawMockMaker(history, checkingProxer(checker, proxer))));
    WildcardSupport wildcardSupport = wildcardSupport(
        history,
        tokenizer(proxer),
        repairer(checker),
        wildcardMatcherizer(formatter),
        formatter);

    Configuration configuration = configuration()
        .history(history)
        .checker(checker)
        .pageFormatter(pageFormatter)
        .exception(exception)
        .overrider(overrider)
        .mockNamer(uniqueNamer(history))
        .mockMaker(mockMaker)
        .injector(injector(singletonArray(chain(randomPrimitiveMaker(), finalMaker(), mockMaker))))
        .wildcardSupport(wildcardSupport)
        .verifier(verifier(proxer, overrider, pageFormatter, wildcardSupport, history))
        .validate();

    return checking(checker, proxer, purging(history, proxer, configurableFacade(configuration)));
  }
}
