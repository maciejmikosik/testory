package org.testory;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class describe_building {
  @Test
  public void source_code_is_available_at_runtime() throws IOException {
    String sourceFile = Testory.class.getSimpleName() + ".java";
    InputStream stream = Testory.class.getResourceAsStream(sourceFile);
    assertNotNull(stream);
    stream.close();
  }
}
