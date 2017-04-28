package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.testory.Testory.given;
import static org.testory.testing.Fakes.newObject;
import static org.testory.testing.Fakes.newThrowable;

import org.junit.Before;
import org.junit.Test;
import org.testory.common.Closure;
import org.testory.common.VoidClosure;

public class TestGiven {
  private Object object;
  private int invoked;
  private Throwable throwable;

  @Before
  public void before() {
    object = newObject("object");
    throwable = newThrowable("throwable");
  }

  @Test
  public void returns_same_object() {
    Object given = given(object);
    assertSame(object, given);
  }

  @Test
  public void returns_null_object() {
    Object given = given((Object) null);
    assertSame(null, given);
  }

  @Test
  public void returns_same_object_of_final_class() {
    final class FinalClass {}
    object = new FinalClass();
    Object given = given(object);
    assertSame(object, given);
  }

  @Test
  public void closure_is_invoked() {
    given(new Closure() {
      public Object invoke() throws Throwable {
        invoked++;
        return null;
      }
    });
    assertEquals(1, invoked);
  }

  @Test
  public void closure_cannot_throw_throwable() {
    try {
      given(new Closure() {
        public Object invoke() throws Throwable {
          throw throwable;
        }
      });
      fail();
    } catch (TestoryException e) {
      assertEquals(throwable, e.getCause());
    }
  }

  @Test
  public void closure_cannot_be_null() {
    try {
      given((Closure) null);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void void_closure_is_invoked() {
    given(new VoidClosure() {
      public void invoke() throws Throwable {
        invoked++;
      }
    });
    assertEquals(1, invoked);
  }

  @Test
  public void void_closure_cannot_throw_throwable() {
    try {
      given(new VoidClosure() {
        public void invoke() throws Throwable {
          throw throwable;
        }
      });
      fail();
    } catch (TestoryException e) {
      assertEquals(throwable, e.getCause());
    }
  }

  @Test
  public void void_closure_cannot_be_null() {
    try {
      given((VoidClosure) null);
      fail();
    } catch (TestoryException e) {}
  }
}
