package org.testory.extra;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.any;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.onInstance;
import static org.testory.Testory.the;
import static org.testory.Testory.willReturn;
import static org.testory.extra.GivenFile.givenIsAbsent;
import static org.testory.extra.GivenFile.givenIsAbsolute;
import static org.testory.extra.GivenFile.givenIsCanonical;
import static org.testory.extra.GivenFile.givenIsChildOf;
import static org.testory.extra.GivenFile.givenIsDirectory;
import static org.testory.extra.GivenFile.givenIsFile;
import static org.testory.extra.GivenFile.givenIsLinkTo;
import static org.testory.extra.GivenFile.givenIsNamed;
import static org.testory.extra.GivenFile.givenIsRelativeOf;
import static org.testory.extra.GivenFile.givenIsRoot;
import static org.testory.extra.GivenFile.givenIsSane;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.testory.TestoryException;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;

public class Describe_GivenFile {
  private String name, path;
  private FileFilter fileFilter;
  private FilenameFilter filenameFilter;

  private File file, directory, absent;
  private File parent, child, childA, childB, childC;
  private File absolute, relative;
  private File canonical, target, link, root;
  private File accepted, rejected;

  @Before
  public void before() {
    name = "name";
    path = "pathA/pathB/pathC";
    fileFilter = mock(FileFilter.class);
    filenameFilter = mock(FilenameFilter.class);

    file = strictMock(File.class);
    directory = strictMock(File.class);
    absent = strictMock(File.class);
    parent = strictMock(File.class);
    child = strictMock(File.class);
    childA = strictMock(File.class);
    childB = strictMock(File.class);
    childC = strictMock(File.class);
    absolute = strictMock(File.class);
    relative = strictMock(File.class);
    canonical = strictMock(File.class);
    target = strictMock(File.class);
    link = strictMock(File.class);
    root = strictMock(File.class);
    accepted = strictMock(File.class);
    rejected = strictMock(File.class);
  }

  @Test
  public void named_file_has_name() {
    givenIsNamed(name, file);
    assertEquals(name, file.getName());
  }

  @Test
  public void file_is_file() {
    givenIsFile(file);
    assertTrue(file.isFile());
  }

  @Test
  public void file_is_not_directory() {
    givenIsFile(file);
    assertFalse(file.isDirectory());
  }

  @Test
  public void file_exists() {
    givenIsFile(file);
    assertTrue(file.exists());
  }

  @Test
  public void file_has_no_children() {
    givenIsFile(file);
    assertArrayEquals(null, file.listFiles());
  }

  @Test
  public void file_is_deletable() {
    givenIsFile(file);
    assertTrue(file.delete());
  }

  @Test
  public void file_is_not_makeable() {
    givenIsFile(file);
    assertFalse(file.mkdir());
  }

  @Test
  public void file_is_not_makeable_with_parents() {
    givenIsFile(file);
    assertFalse(file.mkdirs());
  }

  @Test
  public void directory_is_directory() {
    givenIsDirectory(directory);
    assertTrue(directory.isDirectory());
  }

  @Test
  public void directory_is_not_file() {
    givenIsDirectory(directory);
    assertFalse(directory.isFile());
  }

  @Test
  public void directory_exists() {
    givenIsDirectory(directory);
    assertTrue(directory.exists());
  }

  @Test
  public void directory_is_empty() {
    givenIsDirectory(directory);
    assertArrayEquals(new File[0], directory.listFiles());
  }

  @Test
  public void directory_is_deletable() {
    givenIsDirectory(directory);
    assertTrue(directory.delete());
  }

  @Test
  public void directory_is_not_makeable() {
    givenIsDirectory(directory);
    assertFalse(directory.mkdir());
  }

  @Test
  public void directory_is_not_makeable_with_parents() {
    givenIsDirectory(directory);
    assertFalse(directory.mkdirs());
  }

  @Test
  public void absent_does_not_exist() {
    givenIsAbsent(absent);
    assertFalse(absent.exists());
  }

  @Test
  public void absent_is_not_file() {
    givenIsAbsent(absent);
    assertFalse(absent.isFile());
  }

  @Test
  public void absent_is_not_directory() {
    givenIsAbsent(absent);
    assertFalse(absent.isDirectory());
  }

  @Test
  public void absent_has_no_children() {
    givenIsAbsent(absent);
    assertArrayEquals(null, absent.listFiles());
  }

  @Test
  public void absent_is_not_deletable() {
    givenIsAbsent(absent);
    assertFalse(absent.delete());
  }

  @Test
  public void absent_is_makeable() {
    givenIsAbsent(absent);
    assertTrue(absent.mkdir());
  }

  @Test
  public void absent_is_makeable_with_parents() {
    givenIsAbsent(absent);
    assertTrue(absent.mkdirs());
  }

  @Test
  public void child_has_parent() {
    givenIsDirectory(parent);
    givenIsChildOf(parent, child);
    assertSame(parent, child.getParentFile());
  }

  @Test
  public void parent_has_child() {
    givenIsDirectory(parent);
    givenIsChildOf(parent, child);
    assertThat(parent.listFiles(), arrayContainingSameInAnyOrder(child));
  }

  @Test
  public void parent_has_children() {
    givenIsDirectory(parent);
    givenIsChildOf(parent, childA);
    givenIsChildOf(parent, childB);
    givenIsChildOf(parent, childC);
    assertThat(parent.listFiles(), arrayContainingSameInAnyOrder(childA, childB, childC));
  }

  @Test
  public void parent_must_be_directory() {
    given(willReturn(false), parent).isDirectory();
    try {
      givenIsChildOf(parent, child);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void absolute_is_absolute() {
    givenIsAbsolute(absolute);
    assertTrue(absolute.isAbsolute());
    assertSame(absolute, absolute.getAbsoluteFile());
  }

  @Test
  public void relative_is_not_absolute() {
    givenIsRelativeOf(absolute, relative);
    assertFalse(relative.isAbsolute());
    assertSame(absolute, relative.getAbsoluteFile());
  }

  @Test
  public void relative_delegates_getName() {
    givenIsRelativeOf(absolute, relative);
    given(willReturn(name), absolute).getName();
    assertEquals(name, relative.getName());
  }

  @Test
  public void relative_delegates_isFile() {
    givenIsRelativeOf(absolute, relative);

    given(willReturn(true), absolute).isFile();
    assertTrue(relative.isFile());
    given(willReturn(false), absolute).isFile();
    assertFalse(relative.isFile());
  }

  @Test
  public void relative_delegates_isDirectory() {
    givenIsRelativeOf(absolute, relative);

    given(willReturn(true), absolute).isDirectory();
    assertTrue(relative.isDirectory());
    given(willReturn(false), absolute).isDirectory();
    assertFalse(relative.isDirectory());
  }

  @Test
  public void relative_delegates_exist() {
    givenIsRelativeOf(absolute, relative);

    given(willReturn(true), absolute).exists();
    assertTrue(relative.exists());
    given(willReturn(false), absolute).exists();
    assertFalse(relative.exists());
  }

  @Test
  public void relative_delegates_getCanonicalFile() throws IOException {
    givenIsRelativeOf(absolute, relative);
    given(willReturn(canonical), absolute).getCanonicalFile();
    given(willReturn(canonical), absolute).getCanonicalFile();
    assertSame(canonical, relative.getCanonicalFile());
  }

  @Test
  public void relative_delegates_delete() {
    givenIsRelativeOf(absolute, relative);

    given(willReturn(true), absolute).delete();
    assertTrue(relative.delete());
    given(willReturn(false), absolute).delete();
    assertFalse(relative.delete());
  }

  @Test
  public void canonical_is_canonical() throws IOException {
    givenIsCanonical(canonical);
    assertSame(canonical, canonical.getCanonicalFile());
  }

  @Test
  public void link_exists() {
    givenIsLinkTo(target, link);
    assertTrue(link.exists());
  }

  @Test
  public void link_is_deletable() {
    givenIsLinkTo(target, link);
    assertTrue(link.delete());
  }

  @Test
  public void link_delegates_getCanonicalFile() throws IOException {
    givenIsLinkTo(target, link);
    given(willReturn(canonical), target).getCanonicalFile();
    assertSame(canonical, link.getCanonicalFile());
  }

  @Test
  public void link_delegates_isFile() {
    givenIsLinkTo(target, link);

    given(willReturn(true), target).isFile();
    assertTrue(link.isFile());
    given(willReturn(false), target).isFile();
    assertFalse(link.isFile());
  }

  @Test
  public void link_delegates_isDirectory() {
    givenIsLinkTo(target, link);

    given(willReturn(true), target).isDirectory();
    assertTrue(link.isDirectory());
    given(willReturn(false), target).isDirectory();
    assertFalse(link.isDirectory());
  }

  @Test
  public void link_delegates_children() {
    givenIsLinkTo(target, link);
    given(willReturn(new File[] { child }), target).listFiles();
    assertThat(link.listFiles(), arrayContainingSameInAnyOrder(child));
  }

  @Test
  public void root_has_no_parent() {
    givenIsRoot(root);
    assertNull(root.getParentFile());
  }

  @Test
  public void sane_delegates_path() {
    givenIsSane(file);

    given(willReturn(parent), file).getParentFile();
    given(willReturn(path), parent).getPath();
    given(willReturn(name), file).getName();
    assertEquals(path + "/" + name, file.getPath());

    given(willReturn(null), file).getParentFile();
    given(willReturn(name), file).getName();
    assertEquals(name, file.getPath());
  }

  @Test
  public void sane_delegates_getAbsolutePath() {
    givenIsSane(file);
    given(willReturn(absolute), file).getAbsoluteFile();
    given(willReturn(path), absolute).getPath();
    assertEquals(path, file.getAbsolutePath());
  }

  @Test
  public void sane_delegates_getParent() {
    givenIsSane(file);
    given(willReturn(parent), file).getParentFile();
    given(willReturn(path), parent).getPath();
    assertEquals(path, file.getParent());
  }

  @Test
  public void sane_delegates_getCanonicalPath() throws IOException {
    givenIsSane(file);
    given(willReturn(canonical), file).getCanonicalFile();
    given(willReturn(path), canonical).getPath();
    assertEquals(path, file.getCanonicalPath());
  }

  @Test
  public void sane_delegates_listFiles_FileFilter() {
    givenIsSane(directory);

    given(willReturn(new File[] { accepted, rejected }), directory).listFiles();
    given(willReturn(true), fileFilter).accept(any(File.class, sameInstance(accepted)));
    given(willReturn(false), fileFilter).accept(any(File.class, sameInstance(rejected)));
    assertThat(directory.listFiles(fileFilter), arrayContainingSameInAnyOrder(accepted));

    given(willReturn(null), directory).listFiles();
    assertArrayEquals(null, directory.listFiles(fileFilter));
  }

  @Test
  public void sane_delegates_listFiles_FilenameFilter() {
    givenIsSane(directory);

    given(willReturn(new File[] { accepted, rejected }), directory).listFiles();
    given(willReturn(directory), accepted).getParentFile();
    given(willReturn(directory), rejected).getParentFile();
    given(willReturn("accepted"), accepted).getName();
    given(willReturn("rejected"), rejected).getName();
    given(willReturn(true), filenameFilter).accept(the(directory), accepted.getName());
    given(willReturn(false), filenameFilter).accept(the(directory), rejected.getName());
    assertThat(directory.listFiles(filenameFilter), arrayContainingSameInAnyOrder(accepted));

    given(willReturn(null), directory).listFiles();
    assertArrayEquals(null, directory.listFiles(filenameFilter));
  }

  @Test
  public void sane_delegates_list() {
    givenIsSane(directory);

    given(willReturn(new File[] { childA, childB }), directory).listFiles();
    given(willReturn("childA"), childA).getName();
    given(willReturn("childB"), childB).getName();
    assertThat(directory.list(), arrayContainingSameInAnyOrder(childA.getName(), childB.getName()));

    given(willReturn(null), directory).listFiles();
    assertArrayEquals(null, directory.list());
  }

  @Test
  public void sane_delegates_list_FilenameFilter() {
    givenIsSane(directory);

    given(willReturn(new File[] { accepted, rejected }), directory).listFiles();
    given(willReturn(directory), accepted).getParentFile();
    given(willReturn(directory), rejected).getParentFile();
    given(willReturn("accepted"), accepted).getName();
    given(willReturn("rejected"), rejected).getName();
    given(willReturn(true), filenameFilter).accept(the(directory), accepted.getName());
    given(willReturn(false), filenameFilter).accept(the(directory), rejected.getName());
    assertThat(directory.list(filenameFilter), arrayContainingSameInAnyOrder(accepted.getName()));

    given(willReturn(null), directory).listFiles();
    assertArrayEquals(null, directory.list(filenameFilter));
  }

  @Test
  public void sane_delegates_toString() {
    givenIsSane(file);
    given(willReturn(name), file).getName();
    assertEquals(name, file.toString());
  }

  private static <T> T strictMock(Class<T> type) {
    T mock = mock(type);
    given(new Handler() {
      public Object handle(Invocation invocation) {
        throw new RuntimeException("unstubbed method " + invocation.method.getName());
      }
    }, onInstance(mock));
    return mock;
  }

  private static Matcher<Object[]> arrayContainingSameInAnyOrder(final Object... elements) {
    return new TypeSafeMatcher<Object[]>() {
      protected boolean matchesSafely(Object[] items) {
        if (elements.length != items.length) {
          return false;
        }
        return containsSameElements(elements, items);
      }

      private boolean containsSameElements(final Object[] arrayA, Object[] arrayB) {
        List<Object> listA = new ArrayList<Object>(asList(arrayA));
        List<Object> actuals = new ArrayList<Object>(asList(arrayB));
        loop: while (!listA.isEmpty()) {
          for (Iterator<Object> iterator = actuals.iterator(); iterator.hasNext();) {
            if (listA.get(0) == iterator.next()) {
              iterator.remove();
              listA.remove(0);
              continue loop;
            }
          }
          return false;
        }
        return true;
      }

      public void describeTo(Description description) {
        description.appendText("array of size " + elements.length);
      }

      protected void describeMismatchSafely(Object[] item, Description mismatchDescription) {
        mismatchDescription.appendText("was array of size " + item.length);
      }
    };
  }
}
