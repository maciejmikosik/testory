package org.testory.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.testory.common.Throwables.newLinkageError;

import org.junit.Before;
import org.junit.Test;

public class Describe_Throwables_newLinkageError {
  private Throwable cause;
  private LinkageError error;

  @Before
  public void before() {
    cause = new Throwable();
  }

  @Test
  public void intializes_cause() {
    error = newLinkageError(cause);
    assertEquals(cause, error.getCause());
  }

  @Test
  public void leaves_empty_message() {
    error = newLinkageError(cause);
    assertNull(error.getMessage());
  }

  @Test
  public void requires_non_null_cause() {
    try {
      newLinkageError(null);
      fail();
    } catch (NullPointerException e) {}
  }
}
