package org.testory.doc;

/**
 * <p>
 * Testory makes your tests human-readable by using given-when-then idiom advised by <a
 * href="http://en.wikipedia.org/wiki/Behavior_Driven_Development">Behavior Driven Development</a>.
 * </p>
 * <p>
 * With Testory you can decorate your code adding <b>given</b>, <b>when</b> and <b>then</b>
 * keywords, where <b>then</b> works like junit's assertTrue.
 * 
 * <pre>
 * given(list = new ArrayList&lt;String&gt;());
 * when(list.add(&quot;element&quot;));
 * then(!list.isEmpty());
 * </pre>
 * 
 * </p>
 * <p>
 * You can assert value returned in <b>when</b>...
 * 
 * <pre>
 * given(list = new ArrayList&lt;String&gt;());
 * given(list.add(&quot;element&quot;));
 * when(list.get(0));
 * thenReturned(&quot;element&quot;);
 * </pre>
 * 
 * </p>
 * <p>
 * or assert that exception was thrown.
 * 
 * <pre>
 * given(list = new ArrayList&lt;String&gt;());
 * when(list).get(0);
 * thenThrown(IndexOutOfBoundsException.class);
 * </pre>
 * 
 * </p>
 * <p>
 * Tests can be even more compact using matchers ...
 * 
 * <pre>
 * given(list = new ArrayList&lt;String&gt;());
 * given(list.add(&quot;element&quot;));
 * when(list.clone());
 * thenReturned(not(sameInstance(list)));
 * </pre>
 * 
 * </p>
 * <p>
 * or handy macros.
 * 
 * <pre>
 * given(list = new ArrayList&lt;String&gt;());
 * givenTimes(5, list).add(&quot;element&quot;);
 * when(list.size());
 * thenReturned(5);
 * </pre>
 * 
 * </p>
 * See {@link TestoryTutorial tutorial} for complete list of features.
 */
public class TestoryDemo {}
