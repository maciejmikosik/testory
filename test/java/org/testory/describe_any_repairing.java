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

public class describe_any_repairing {
  private Object a, b, x;
  private int i, j, k;
  private Mockable mock;

  @Before
  public void before() {
    mock = mock(Mockable.class);
    a = newObject("a");
    b = newObject("b");
    x = newObject("x");
    i = 123;
    j = 456;
    k = 789;
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
  public void solves_wrappers() {
    given(willReturn(true), mock).objects(a, any(int.class, same(b)), a);

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
  public void solves_unsurrounded_primitive_types() {
    given(willReturn(true), mock).objects(a, a, any(Integer.class, equal(i)),
        any(Integer.class, equal(i)), a, a);

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
  public void solves_varargs() {
    given(willReturn(true), mock).varargs(a, a, any(Object.class, same(b)), a);

    assertTrue(mock.varargs(a, a, b, a));

    assertFalse(mock.varargs(x, a, b, a));
    assertFalse(mock.varargs(a, x, b, a));
    assertFalse(mock.varargs(a, a, x, a));
    assertFalse(mock.varargs(a, a, b, x));
  }

  @Test
  public void solves_primitive_varargs() {
    given(willReturn(true), mock).primitiveVarargs(any(int.class, equal(i)),
        any(int.class, equal(i)), any(int.class, equal(j)), any(int.class, equal(i)));

    assertTrue(mock.primitiveVarargs(i, i, j, i));

    assertFalse(mock.primitiveVarargs(k, i, j, i));
    assertFalse(mock.primitiveVarargs(i, k, j, i));
    assertFalse(mock.primitiveVarargs(i, i, k, i));
    assertFalse(mock.primitiveVarargs(i, i, j, k));
  }

  @Test
  public void solves_varargs_for_explicit_object() {
    given(willReturn(true), mock).varargs(a, any(Object.class));

    assertFalse(mock.varargs(a));
    assertTrue(mock.varargs(a, a));
    assertFalse(mock.varargs(a, a, a));
  }

  @Test
  public void solves_varargs_for_explicit_array() {
    given(willReturn(true), mock).varargs(a, any(Object[].class));

    assertTrue(mock.varargs(a));
    assertTrue(mock.varargs(a, b));
    assertTrue(mock.varargs(a, b, b));
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

  private static abstract class Mockable {
    abstract boolean objects(Object a, Object b, Object c);

    abstract boolean objects(Object a, Object b, int c, int d, Object e, Object f);

    abstract boolean varargs(Object a, Object... os);

    abstract boolean primitiveVarargs(int i, int... is);
  }
}
