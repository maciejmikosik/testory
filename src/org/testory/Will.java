package org.testory;

import org.testory.common.Nullable;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;

public interface Will extends Handler {
  @Nullable
  Object handle(Invocation invocation) throws Throwable;
}
