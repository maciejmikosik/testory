package org.testory.common;

import java.io.Closeable;
import java.io.IOException;

public class Closeables {
  public static void closeQuietly(Closeable closeable) {
    try {
      if (closeable != null) {
        closeable.close();
      }
    } catch (IOException e) {}
  }
}
