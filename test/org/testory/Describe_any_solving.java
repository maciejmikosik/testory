package org.testory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.any;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;
import static org.testory.test.Testilities.newObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Describe_any_solving {
  private Object a, b, x;
  private Mock mock;

  @Before
  public void before() {
    mock = mock(Mock.class);
    a = newObject("a");
    b = newObject("b");
    x = newObject("x");
  }

  @After
  public void after() {
    when("");
    when("");
  }

  @Test
  public void solves_objects() {
    given(willReturn(true), mock).objects(a, any(Object.class, same(b)), a);

    assertTrue(mock.objects(a, b, a));

    assertFalse(mock.objects(x, b, a));
    assertFalse(mock.objects(a, x, a));
    assertFalse(mock.objects(a, b, x));
  }

  @Test
  public void cannot_solve_final_type() {
    try {
      given(willReturn(true), mock).objects(a, any(String.class), a);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void solves_surrounded_final_types() {
    given(willReturn(true), mock).objects(a, any(Object.class, same(a)),
        any(String.class, same(b)), any(String.class, same(b)), any(Object.class, same(a)), a);

    assertTrue(mock.objects(a, a, b, b, a, a));

    assertFalse(mock.objects(x, a, b, b, a, a));
    assertFalse(mock.objects(a, x, b, b, a, a));
    assertFalse(mock.objects(a, a, x, b, a, a));
    assertFalse(mock.objects(a, a, b, x, a, a));
    assertFalse(mock.objects(a, a, b, b, x, a));
    assertFalse(mock.objects(a, a, b, b, a, x));
  }

  @Test
  public void cannot_solve_more_anys_than_parameters() {
    try {
      any(Object.class);
      given(willReturn(true), mock)
          .objects(any(Object.class), any(Object.class), any(Object.class));
    } catch (TestoryException e) {}
  }

  @Test
  public void cannot_solve_varargs() {
    try {
      given(willReturn(true), mock)
          .varargs(any(Object.class), any(Object.class), any(Object.class));
    } catch (TestoryException e) {}
  }

  private static Object same(final Object object) {
    return new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object item) {
        return object == item;
      }

      public String toString() {
        return "same(" + object + ")";
      }
    };
  }

  public interface Mock {
    boolean objects(Object a, Object b, Object c);

    boolean objects(Object a, Object b, Object c, Object d, Object e, Object f);

    boolean varargs(Object a, Object... os);
  }
}
