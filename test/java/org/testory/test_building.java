package org.testory;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static javax.xml.bind.DatatypeConverter.printHexBinary;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import org.junit.Test;

public class test_building {
  private String hashA, hashB;

  @Test
  public void source_code_is_available_at_runtime() throws IOException {
    String sourceFile = Testory.class.getSimpleName() + ".java";
    InputStream stream = Testory.class.getResourceAsStream(sourceFile);
    assertNotNull(stream);
    stream.close();
  }

  @Test
  public void build_is_deterministic() throws Exception {
    exec("./run/build");
    hashA = sha1(".sink/testory.jar");
    exec("./run/build");
    hashB = sha1(".sink/testory.jar");

    assertEquals(hashA, hashB);
  }

  private static void exec(String command) {
    try {
      Process process = Runtime.getRuntime().exec(command);
      ExecutorService executor = newFixedThreadPool(2);
      executor.submit(forward(process.getInputStream(), System.out));
      executor.submit(forward(process.getErrorStream(), System.err));
      int exitCode = process.waitFor();
      assume(exitCode == 0);
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private static Callable<Void> forward(final InputStream input, final PrintStream output) {
    return new Callable<Void>() {
      public Void call() throws IOException {
        int data;
        while ((data = input.read()) != -1) {
          output.write(data);
        }
        return null;
      }
    };
  }

  private static String sha1(String filename) {
    try {
      MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
      DigestInputStream input = new DigestInputStream(new FileInputStream(filename), sha1);
      drainAllBytes(input);
      input.close();
      return printHexBinary(input.getMessageDigest().digest());
    } catch (IOException | NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  private static void drainAllBytes(DigestInputStream input) throws IOException {
    byte[] buffer = new byte[1024];
    for (int read = 0; read > -1; read = input.read(buffer)) {}
  }

  private static void assume(boolean condition) {
    if (!condition) {
      throw new RuntimeException();
    }
  }
}
