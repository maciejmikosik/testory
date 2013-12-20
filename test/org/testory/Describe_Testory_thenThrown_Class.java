package org.testory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.test.Testilities.newObject;
import static org.testory.test.Testilities.newThrowable;
import static org.testory.test.Testilities.printStackTrace;
import static org.testory.test.Testilities.returning;
import static org.testory.test.Testilities.throwing;

import org.junit.Before;
import org.junit.Test;
import org.testory.test.Testilities.Invoker;

@SuppressWarnings("serial")
public class Describe_Testory_thenThrown_Class {
  private Throwable throwable;
  private Object object;
  private Invoker invoker;

  @Before
  public void before() {
    throwable = newThrowable("throwable");
    object = newObject("object");
    invoker = new Invoker();
  }

  @Test
  public void should_succeed_if_closure_thrown_same_type() {
    class ExpectedThrowable extends Throwable {}
    when(throwing(new ExpectedThrowable()));
    thenThrown(ExpectedThrowable.class);
  }

  @Test
  public void should_succeed_if_proxy_thrown_same_type() throws Throwable {
    class ExpectedThrowable extends Throwable {}
    when(invoker).invoke(throwing(new ExpectedThrowable()));
    thenThrown(ExpectedThrowable.class);
  }

  @Test
  public void should_succeed_if_closure_thrown_subtype() {
    class ExpectedThrowable extends Throwable {}
    class SubThrowable extends ExpectedThrowable {}
    when(throwing(new SubThrowable()));
    thenThrown(ExpectedThrowable.class);
  }

  @Test
  public void should_succeed_if_proxy_thrown_subtype() throws Throwable {
    class ExpectedThrowable extends Throwable {}
    class SubThrowable extends ExpectedThrowable {}
    when(invoker).invoke(throwing(new SubThrowable()));
    thenThrown(ExpectedThrowable.class);
  }

  @Test
  public void should_fail_if_closure_thrown_supertype() {
    class SuperThrowable extends Throwable {}
    class ExpectedThrowable extends SuperThrowable {}
    throwable = new SuperThrowable();
    when(throwing(throwable));
    try {
      thenThrown(ExpectedThrowable.class);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected thrown instance of\n" //
          + "    " + ExpectedThrowable.class.getName() + "\n" //
          + "  but thrown instance of\n" //
          + "    " + SuperThrowable.class.getName() + "\n" //
          + "\n" //
          + printStackTrace(throwable) + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_proxy_thrown_supertype() throws Throwable {
    class SuperThrowable extends Throwable {}
    class ExpectedThrowable extends SuperThrowable {}
    throwable = new SuperThrowable();
    when(invoker).invoke(throwing(throwable));
    try {
      thenThrown(ExpectedThrowable.class);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected thrown instance of\n" //
          + "    " + ExpectedThrowable.class.getName() + "\n" //
          + "  but thrown instance of\n" //
          + "    " + SuperThrowable.class.getName() + "\n" //
          + "\n" //
          + printStackTrace(throwable) + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_closure_thrown_not_related_type() {
    class ExpectedThrowable extends Throwable {}
    class OtherThrowable extends Throwable {}
    throwable = new OtherThrowable();
    when(throwing(throwable));
    try {
      thenThrown(ExpectedThrowable.class);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected thrown instance of\n" //
          + "    " + ExpectedThrowable.class.getName() + "\n" //
          + "  but thrown instance of\n" //
          + "    " + OtherThrowable.class.getName() + "\n" //
          + "\n" //
          + printStackTrace(throwable) + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_proxy_thrown_not_related_type() throws Throwable {
    class ExpectedThrowable extends Throwable {}
    class OtherThrowable extends Throwable {}
    throwable = new OtherThrowable();
    when(invoker).invoke(throwing(throwable));
    try {
      thenThrown(ExpectedThrowable.class);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected thrown instance of\n" //
          + "    " + ExpectedThrowable.class.getName() + "\n" //
          + "  but thrown instance of\n" //
          + "    " + OtherThrowable.class.getName() + "\n" //
          + "\n" //
          + printStackTrace(throwable) + "\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_closure_returned() {
    class ExpectedThrowable extends Throwable {}
    when(returning(object));
    try {
      thenThrown(ExpectedThrowable.class);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected thrown instance of\n" //
          + "    " + ExpectedThrowable.class.getName() + "\n" //
          + "  but returned\n" //
          + "    object\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_proxy_returned() throws Throwable {
    class ExpectedThrowable extends Throwable {}
    when(invoker).invoke(returning(object));
    try {
      thenThrown(ExpectedThrowable.class);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals("\n" //
          + "  expected thrown instance of\n" //
          + "    " + ExpectedThrowable.class.getName() + "\n" //
          + "  but returned\n" //
          + "    object\n" //
      , e.getMessage());
    }
  }

  @Test
  public void should_fail_if_expected_null_type() {
    try {
      thenThrown((Class<? extends Throwable>) null);
      fail();
    } catch (TestoryException e) {}
  }
}
