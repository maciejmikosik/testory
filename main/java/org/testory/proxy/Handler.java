package org.testory.proxy;

import org.testory.common.Nullable;

public interface Handler {
  @Nullable
  Object handle(Invocation invocation) throws Throwable;
}
