package org.testory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.testory.Testory.any;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;
import static org.testory.common.Objects.areEqualDeep;
import static org.testory.test.Testilities.newObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Describe_any_solving {
  private Object a, b, x;
  private int i, j;
  private Mock mock;

  @Before
  public void before() {
    mock = mock(Mock.class);
    a = newObject("a");
    b = newObject("b");
    x = newObject("x");
    i = 123;
    j = 456;
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
  public void solves_final_type() {
    given(willReturn(true), mock).objects(a, any(FinalClass.class, same(b)), a);

    assertTrue(mock.objects(a, b, a));

    assertFalse(mock.objects(x, b, a));
    assertFalse(mock.objects(a, x, a));
    assertFalse(mock.objects(a, b, x));
  }

  @Test
  public void solves_array() {
    given(willReturn(true), mock).objects(a, any(Object[].class, same(b)), a);

    assertTrue(mock.objects(a, b, a));

    assertFalse(mock.objects(x, b, a));
    assertFalse(mock.objects(a, x, a));
    assertFalse(mock.objects(a, b, x));
  }

  @Test
  public void solves_surrounded_primitive_types() {
    given(willReturn(true), mock).objects(a, any(Object.class, same(a)),
        any(Integer.class, equal(i)), any(Integer.class, equal(i)), any(Object.class, same(a)), a);

    assertTrue(mock.objects(a, a, i, i, a, a));

    assertFalse(mock.objects(x, a, i, i, a, a));
    assertFalse(mock.objects(a, x, i, i, a, a));
    assertFalse(mock.objects(a, a, j, i, a, a));
    assertFalse(mock.objects(a, a, i, j, a, a));
    assertFalse(mock.objects(a, a, i, i, x, a));
    assertFalse(mock.objects(a, a, i, i, a, x));
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

  private static Object equal(final Object object) {
    return new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object item) {
        return areEqualDeep(object, item);
      }

      public String toString() {
        return "equal(" + object + ")";
      }
    };
  }

  private static final class FinalClass {}

  public interface Mock {
    boolean objects(Object a, Object b, Object c);

    boolean objects(Object a, Object b, int c, int d, Object e, Object f);

    boolean varargs(Object a, Object... os);
  }
}
