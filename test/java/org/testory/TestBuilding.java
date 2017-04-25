package org.testory;

import static java.util.Arrays.asList;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static javax.xml.bind.DatatypeConverter.printHexBinary;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import org.junit.Test;

public class TestBuilding {
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
    hashA = sha1("/tmp/testory.jar");
    exec("./run/build");
    hashB = sha1("/tmp/testory.jar");

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

  @Test
  public void api_exposes_only_required_types() {
    assertEquals(Build.exposed, filterNonJdk(allDependencies(Build.exposed)));
  }

  private static Set<Class<?>> allDependencies(Set<Class<?>> classes) {
    Set<Class<?>> dependencies = new HashSet<Class<?>>();
    Set<Class<?>> remaining = new HashSet<Class<?>>();
    remaining.addAll(classes);

    while (!remaining.isEmpty()) {
      Class<?> current = remaining.iterator().next();
      dependencies.add(current);
      remaining.addAll(directDependencies(current));
      remaining.removeAll(dependencies);
    }
    return dependencies;
  }

  private static Set<Class<?>> directDependencies(Class<?> type) {
    Set<Class<?>> dependencies = new HashSet<Class<?>>();
    for (Method method : type.getMethods()) {
      dependencies.add(method.getReturnType());
      dependencies.addAll(asList(method.getParameterTypes()));
      for (Annotation annotation : method.getAnnotations()) {
        dependencies.add(annotation.annotationType());
      }
      for (Annotation[] parameter : method.getParameterAnnotations()) {
        for (Annotation annotation : parameter) {
          dependencies.add(annotation.annotationType());
        }
      }
    }
    if (type.isArray()) {
      dependencies.add(type.getComponentType());
    }
    return dependencies;
  }

  private static Set<Class<?>> filterNonJdk(Set<Class<?>> types) {
    Set<Class<?>> filtered = new HashSet<Class<?>>();
    for (Class<?> type : types) {
      if (!type.isPrimitive() && !type.isArray() && type.getClassLoader() != null) {
        filtered.add(type);
      }
    }
    return filtered;
  }
}
