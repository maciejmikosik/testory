package org.testory;

import static java.util.Arrays.asList;
import static javax.xml.bind.DatatypeConverter.printHexBinary;
import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestBuilding {
  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  private String hashA, hashB;

  @Test
  public void build_is_deterministic() throws Exception {
    exec("./run/build");
    hashA = sha1("/tmp/testory.jar");
    exec("./run/build");
    hashB = sha1("/tmp/testory.jar");

    assertEquals(hashA, hashB);
  }

  private void exec(String command) throws InterruptedException, IOException {
    int exitCode = new ProcessBuilder(command)
        .redirectOutput(folder.newFile())
        .redirectError(folder.newFile())
        .start()
        .waitFor();
    assume(exitCode == 0);
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
