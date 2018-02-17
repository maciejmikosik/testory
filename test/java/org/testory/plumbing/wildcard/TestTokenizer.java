package org.testory.plumbing.wildcard;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.plumbing.wildcard.Tokenizer.tokenizer;
import static org.testory.proxy.proxer.CglibProxer.cglibProxer;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.testory.plumbing.PlumbingException;

public class TestTokenizer {
  private Tokenizer tokenizer;

  @Before
  public void before() {
    tokenizer = tokenizer(cglibProxer());
  }

  @Test
  public void supports_normal_classes() {
    assertSupports(List.class);
    assertSupports(ArrayList.class);
    assertSupports(AbstractList.class);
    assertSupports(Object.class);
    assertSupports(String.class);
  }

  @Test
  public void supports_arrays() {
    assertSupports(Object[].class);
    assertSupports(String[].class);
    assertSupports(int[].class);
    assertSupports(Object[][][].class);
    assertSupports(String[][][].class);
    assertSupports(int[][][].class);
  }

  @Test
  public void supports_wrappers() {
    assertSupports(Integer.class);
    assertSupports(Void.class);
  }

  @Test
  public void support_primitives_by_boxing() {
    assertTrue(Integer.class.isInstance(tokenizer.token(int.class)));
    assertNotSame(tokenizer.token(int.class), tokenizer.token(int.class));

    assertTrue(Void.class.isInstance(tokenizer.token(void.class)));
    assertNotSame(tokenizer.token(void.class), tokenizer.token(void.class));
  }

  @Test
  public void null_cannot_be_type() {
    try {
      tokenizer.token(null);
      fail();
    } catch (PlumbingException e) {}
  }

  private void assertSupports(Class<?> type) {
    String message = type.toString();
    assertTrue(message, type.isInstance(tokenizer.token(type)));
    assertNotSame(message, tokenizer.token(type), tokenizer.token(type));
  }
}
