package org.testory.common;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.common.Closeables.closeQuietly;

import java.io.Closeable;
import java.io.IOException;

import org.junit.Test;

public class TestCloseables {
  private Closeable closeable;
  protected boolean invoked;

  @Test
  public void closes_closeable() {
    closeable = new Closeable() {
      public void close() {
        invoked = true;
      }
    };
    closeQuietly(closeable);
    assertTrue(invoked);
  }

  @Test
  public void swallows_ioexception() {
    closeable = new Closeable() {
      public void close() throws IOException {
        throw new IOException();
      }
    };
    closeQuietly(closeable);
  }

  @Test
  public void propagates_runtime_exception() {
    closeable = new Closeable() {
      public void close() {
        throw new RuntimeException();
      }
    };
    try {
      closeQuietly(closeable);
      fail();
    } catch (RuntimeException e) {}
  }

  @Test
  public void ignores_null() {
    closeQuietly(null);
  }
}
