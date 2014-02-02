package org.testory;

import org.testory.proxy.Invocation;

public interface On {
  boolean matches(Invocation invocation);
}
