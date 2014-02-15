package org.testory;

import org.testory.proxy.Invocation;

public interface Captor {
  boolean matches(Invocation invocation);
}
