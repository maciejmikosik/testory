package org.testory.common;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.common.Chain.chain;
import static org.testory.testing.Fakes.newObject;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

public class TestChain {
  private Chain<Object> chain;
  private int size;
  private Object a, b, c, d;
  private Iterator<Object> iterator;

  @Before
  public void before() {
    a = newObject("a");
    b = newObject("b");
    c = newObject("c");
    d = newObject("d");
  }

  @Test
  public void empty_chain_has_size_zero() {
    chain = chain();
    size = chain.size();
    assertEquals(0, size);
  }

  @Test
  public void empty_chain_has_no_element() {
    chain = chain();
    try {
      chain.get();
      fail();
    } catch (NoSuchElementException e) {}
  }

  @Test
  public void adding_element_increases_size() {
    chain = chain().add(a).add(b).add(c);
    assertEquals(3, chain.size());
  }

  @Test
  public void adding_same_element_increases_size() {
    chain = chain().add(a).add(a).add(a);
    assertEquals(3, chain.size());
  }

  @Test
  public void adds_element_to_head() {
    chain = chain().add(a).add(b).add(c);
    assertEquals(c, chain.get());
  }

  @Test
  public void adding_does_not_change_original_chain() {
    chain = chain().add(a);
    chain.add(b);
    assertEquals(chain().add(a), chain);
  }

  @Test
  public void adding_forbids_null_elements() {
    chain = chain();
    try {
      chain.add(null);
      fail();
    } catch (NullPointerException e) {}
  }

  @Test
  public void adds_all_elements() {
    chain = chain().addAll(asList(a, b, c));
    assertEquals(chain().add(a).add(b).add(c), chain);
  }

  @Test
  public void add_all_forbids_null_elements() {
    chain = chain();
    try {
      chain.addAll(asList(a, null, c));
      fail();
    } catch (NullPointerException e) {}
  }

  @Test
  public void add_all_forbids_null_iterables() {
    chain = chain();
    try {
      chain.addAll(null);
      fail();
    } catch (NullPointerException e) {}
  }

  @Test
  public void removing_element_decreases_size() {
    chain = chain().add(a).add(b).add(c).remove();
    assertEquals(2, chain.size());
  }

  @Test
  public void removes_element_from_head() {
    chain = chain().add(a).add(b).add(c).add(d).remove();
    assertEquals(c, chain.get());
  }

  @Test
  public void removes_all_elements() {
    chain = chain().add(a).add(b).remove().remove();
    assertEquals(0, chain.size());
  }

  @Test
  public void removing_does_not_change_original_chain() {
    chain = chain().add(a).add(b);
    chain.remove();
    assertEquals(chain().add(a).add(b), chain);
  }

  @Test
  public void removing_is_forbidden_for_empty_chain() {
    chain = chain();
    try {
      chain.remove();
      fail();
    } catch (NoSuchElementException e) {}
  }

  @Test
  public void reverses_empty_chain() {
    chain = chain().reverse();
    assertEquals(0, chain.size());
  }

  @Test
  public void reverses_one_element_chain() {
    chain = chain().add(a).reverse();
    assertEquals(1, chain.size());
    assertSame(a, chain.get());
  }

  @Test
  public void reverses_chain() {
    chain = chain().add(a).add(b).add(c).reverse();
    assertEquals(3, chain.size());
    assertEquals(a, chain.get());
    assertEquals(b, chain.remove().get());
    assertEquals(c, chain.remove().remove().get());
  }

  @Test
  public void iterates_over_empty_chain() {
    iterator = chain().iterator();
    assertFalse(iterator.hasNext());
  }

  @Test
  public void iterates_over_chain() {
    iterator = chain().add(a).add(b).add(c).iterator();
    assertTrue(iterator.hasNext());
    assertSame(c, iterator.next());
    assertTrue(iterator.hasNext());
    assertSame(b, iterator.next());
    assertTrue(iterator.hasNext());
    assertSame(a, iterator.next());
  }

  @Test
  public void iterator_has_end() {
    iterator = chain().add(a).iterator();
    iterator.next();
    try {
      iterator.next();
      fail();
    } catch (NoSuchElementException e) {}
  }

  @Test
  public void iterator_does_not_support_removal() {
    iterator = chain().add(a).iterator();
    iterator.next();
    try {
      iterator.remove();
      fail();
    } catch (UnsupportedOperationException e) {}
  }

  @Test
  public void equals_empty_chain_to_itself() {
    chain = chain();
    assertEquals(chain, chain);
  }

  @Test
  public void equals_empty_chain_to_empty_chain() {
    assertEquals(chain(), chain());
  }

  @Test
  public void equals_not_empty_chain_to_one_element_chain() {
    assertNotEquals(chain(), chain().add(a));
  }

  @Test
  public void equals_not_one_element_chain_to_empty_chain() {
    assertNotEquals(chain().add(a), chain);
  }

  @Test
  public void equals_not_one_element_chains_with_different_elements() {
    assertNotEquals(chain().add(a), chain().add(b));
  }

  @Test
  public void equals_not_chain_to_its_tail() {
    chain = chain().add(a).add(b);
    assertNotEquals(chain, chain.add(c));
  }

  @Test
  public void equals_not_to_object() {
    assertFalse(chain().equals(new Object()));
  }

  @Test
  public void equals_not_to_null() {
    assertFalse(chain().equals(null));
  }

  @Test
  public void implements_hash_code() {
    assertEquals(chain().hashCode(), chain().hashCode());
    assertEquals(chain().add(a).hashCode(), chain().add(a).hashCode());
  }

  @Test
  public void implements_to_string() {
    chain = chain().add(a).add(b).add(c);
    assertTrue(chain.toString().matches(".*c.*b.*a.*"));
  }

  @Test
  public void performs_adding_and_removing_by_reuses_tail() {
    chain = chain().add(a).add(b);
    assertSame(chain, chain.add(c).remove());
  }

  @Test(timeout = 1000)
  public void performs_addition_and_removal() {
    int million = 1000 * 1000;
    chain = chain();
    for (int i = 0; i < million; i++) {
      chain = chain.add(new Object());
    }
    assertEquals(million, chain.size());
    for (int i = 0; i < million; i++) {
      chain = chain.remove();
    }
    assertEquals(0, chain.size());
  }
}
