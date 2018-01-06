package org.testory.testing;

import static org.testory.Testory.given;
import static org.testory.Testory.then;

public class Purging {
  public static void triggerPurge() {
    then(true);
    given(true);
  }
}
