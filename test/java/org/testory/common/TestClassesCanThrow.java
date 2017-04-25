package org.testory.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.common.Classes.canThrow;

import java.lang.reflect.Method;

import org.junit.Test;

public class TestClassesCanThrow {
  @Test
  public void always_can_throw_error() throws Exception {
    assertTrue(canThrow(new Error(), method("throwNothing")));
    assertTrue(canThrow(new Error(), method("throwRuntimeException")));
    assertTrue(canThrow(new Error(), method("throwError")));
    assertTrue(canThrow(new Error(), method("throwException")));
  }

  @Test
  public void always_can_throw_runtime_exception() throws Exception {
    assertTrue(canThrow(new RuntimeException(), method("throwNothing")));
    assertTrue(canThrow(new RuntimeException(), method("throwRuntimeException")));
    assertTrue(canThrow(new RuntimeException(), method("throwError")));
    assertTrue(canThrow(new RuntimeException(), method("throwException")));
  }

  @Test
  public void can_throw_declared_checked_exception() throws Exception {
    assertTrue(canThrow(new Exception(), method("throwException")));
    assertTrue(canThrow(new CheckedException(), method("throwCheckedException")));
  }

  @Test
  public void cannot_throw_undeclared_checked_exception() throws Exception {
    assertFalse(canThrow(new CheckedException(), method("throwNothing")));
    assertFalse(canThrow(new CheckedException(), method("throwOtherCheckedException")));
  }

  @Test
  public void can_throw_subclass_of_legal_throwable() throws Exception {
    assertTrue(canThrow(new CheckedException(), method("throwException")));
    assertTrue(canThrow(new UncheckedException(), method("throwNothing")));
    assertTrue(canThrow(new UncheckedException(), method("throwRuntimeException")));
    assertTrue(canThrow(new UncheckedError(), method("throwNothing")));
    assertTrue(canThrow(new UncheckedError(), method("throwError")));
  }

  @Test
  public void can_throw_anything_if_declared_throwable() throws Exception {
    assertTrue(canThrow(new Throwable(), method("throwThrowable")));
    assertTrue(canThrow(new CheckedException(), method("throwThrowable")));
    assertTrue(canThrow(new UncheckedException(), method("throwThrowable")));
    assertTrue(canThrow(new UncheckedError(), method("throwThrowable")));
  }

  @Test
  public void can_throw_exception_declared_at_any_position() throws Exception {
    assertTrue(canThrow(new CheckedException(), method("throwBothCheckedExceptions")));
    assertTrue(canThrow(new OtherCheckedException(), method("throwBothCheckedExceptions")));
  }

  @Test
  public void throwable_cannot_be_null() throws Exception {
    try {
      canThrow(null, method("throwNothing"));
      fail();
    } catch (NullPointerException e) {}
  }

  @Test
  public void method_cannot_be_null() {
    try {
      canThrow(new Throwable(), null);
      fail();
    } catch (NullPointerException e) {}
  }

  private static Method method(String name) throws SecurityException, NoSuchMethodException {
    return Methods.class.getDeclaredMethod(name);
  }

  static class Methods {
    // @formatter:off
    void throwNothing() {}
    void throwThrowable() throws Throwable{}
    void throwError() throws Error {}
    void throwRuntimeException() throws RuntimeException {}
    void throwUncheckedException() throws UncheckedException {}
    void throwException() throws Exception {}
    void throwCheckedException() throws CheckedException {}
    void throwOtherCheckedException() throws OtherCheckedException {}
    void throwBothCheckedExceptions() throws CheckedException, OtherCheckedException {}
    // @formatter:on
  }

  private static class UncheckedException extends RuntimeException {}

  private static class UncheckedError extends Error {}

  private static class CheckedException extends Exception {}

  private static class OtherCheckedException extends Exception {}
}
