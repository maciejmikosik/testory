package org.testory.extra;

import static java.util.Arrays.asList;
import static org.testory.Testory.any;
import static org.testory.Testory.given;
import static org.testory.Testory.willReturn;
import static org.testory.TestoryException.check;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;

public class GivenFile {
  public static void givenIsNamed(String name, File mock) {
    given(willReturn(name), mock).getName();
  }

  public static void givenIsFile(File mock) {
    given(willReturn(true), mock).isFile();
    given(willReturn(false), mock).isDirectory();
    given(willReturn(true), mock).exists();
    given(willReturn(null), mock).listFiles();
    given(willReturn(true), mock).delete();
    given(willReturn(false), mock).mkdir();
    given(willReturn(false), mock).mkdirs();
  }

  public static void givenIsDirectory(File mock) {
    given(willReturn(true), mock).isDirectory();
    given(willReturn(false), mock).isFile();
    given(willReturn(true), mock).exists();
    given(willReturn(new File[0]), mock).listFiles();
    given(willReturn(true), mock).delete();
    given(willReturn(false), mock).mkdir();
    given(willReturn(false), mock).mkdirs();
  }

  public static void givenIsAbsent(File mock) {
    given(willReturn(false), mock).exists();
    given(willReturn(false), mock).isFile();
    given(willReturn(false), mock).isDirectory();
    given(willReturn(null), mock).listFiles();
    given(willReturn(false), mock).delete();
    given(willReturn(true), mock).mkdir();
    given(willReturn(true), mock).mkdirs();
  }

  public static void givenIsChildOf(File parentMock, File childMock) {
    check(parentMock.isDirectory());
    given(willReturn(parentMock), childMock).getParentFile();
    given(willReturn(addLast(childMock, parentMock.listFiles())), parentMock).listFiles();
  }

  private static <T> T[] addLast(T element, T[] elements) {
    List<T> newList = new ArrayList<T>(asList(elements));
    newList.add(element);
    return newList.toArray(elements);
  }

  public static void givenIsAbsolute(File mock) {
    given(willReturn(true), mock).isAbsolute();
    given(willReturn(mock), mock).getAbsoluteFile();
  }

  public static void givenIsRelativeOf(final File absolute, File relativeMock) {
    given(willReturn(false), relativeMock).isAbsolute();
    given(willReturn(absolute), relativeMock).getAbsoluteFile();
    given(new Handler() {
      public Object handle(Invocation invocation) {
        return absolute.getName();
      }
    }, relativeMock).getName();
    given(new Handler() {
      public Object handle(Invocation invocation) {
        return absolute.isFile();
      }
    }, relativeMock).isFile();
    given(new Handler() {
      public Object handle(Invocation invocation) {
        return absolute.isDirectory();
      }
    }, relativeMock).isDirectory();
    given(new Handler() {
      public Object handle(Invocation invocation) {
        return absolute.exists();
      }
    }, relativeMock).exists();
    try {
      given(new Handler() {
        public Object handle(Invocation invocation) throws IOException {
          return absolute.getCanonicalFile();
        }
      }, relativeMock).getCanonicalFile();
    } catch (IOException e) {
      throw new Error(e);
    }
    given(new Handler() {
      public Object handle(Invocation invocation) {
        return absolute.delete();
      }
    }, relativeMock).delete();
  }

  public static void givenIsCanonical(File mock) {
    try {
      given(willReturn(mock), mock).getCanonicalFile();
    } catch (IOException e) {
      throw new Error(e);
    }
  }

  public static void givenIsLinkTo(final File target, File mock) {
    given(willReturn(true), mock).exists();
    given(willReturn(true), mock).delete();
    try {
      given(new Handler() {
        public Object handle(Invocation invocation) throws IOException {
          return target.getCanonicalFile();
        }
      }, mock).getCanonicalFile();
    } catch (IOException e) {
      throw new Error(e);
    }
    given(new Handler() {
      public Object handle(Invocation invocation) {
        return target.isFile();
      }
    }, mock).isFile();
    given(new Handler() {
      public Object handle(Invocation invocation) {
        return target.isDirectory();
      }
    }, mock).isDirectory();
    given(new Handler() {
      public Object handle(Invocation invocation) {
        return target.listFiles();
      }
    }, mock).listFiles();
  }

  public static void givenIsRoot(File mock) {
    given(willReturn(null), mock).getParentFile();
  }

  public static void givenIsSane(final File mock) {
    given(new Handler() {
      public Object handle(Invocation invocation) {
        return mock.getParentFile() == null
            ? mock.getName()
            : mock.getParentFile().getPath() + "/" + mock.getName();
      }
    }, mock).getPath();
    given(new Handler() {
      public Object handle(Invocation invocation) {
        return mock.getAbsoluteFile().getPath();
      }
    }, mock).getAbsolutePath();
    given(new Handler() {
      public Object handle(Invocation invocation) {
        return mock.getParentFile().getPath();
      }
    }, mock).getParent();
    try {
      given(new Handler() {
        public Object handle(Invocation invocation) throws IOException {
          return mock.getCanonicalFile().getPath();
        }
      }, mock).getCanonicalPath();
    } catch (IOException e) {
      throw new Error(e);
    }
    given(new Handler() {
      public Object handle(Invocation invocation) {
        FileFilter filter = (FileFilter) invocation.arguments.get(0);
        return filter(filter, mock.listFiles());
      }
    }, mock).listFiles(any(FileFilter.class));
    given(new Handler() {
      public Object handle(Invocation invocation) {
        FilenameFilter filter = (FilenameFilter) invocation.arguments.get(0);
        return filter(asFileFilter(filter), mock.listFiles());
      }
    }, mock).listFiles(any(FilenameFilter.class));
    given(new Handler() {
      public Object handle(Invocation invocation) {
        return asNames(mock.listFiles());
      }
    }, mock).list();
    given(new Handler() {
      public Object handle(Invocation invocation) {
        FilenameFilter filter = (FilenameFilter) invocation.arguments.get(0);
        return asNames(filter(asFileFilter(filter), mock.listFiles()));
      }
    }, mock).list(any(FilenameFilter.class));
    given(new Handler() {
      public Object handle(Invocation invocation) {
        return mock.getName();
      }
    }, mock).toString();
  }

  private static FileFilter asFileFilter(final FilenameFilter filter) {
    return new FileFilter() {
      public boolean accept(File file) {
        return filter.accept(file.getParentFile(), file.getName());
      }
    };
  }

  private static File[] filter(FileFilter filter, File[] files) {
    if (files == null) {
      return null;
    }
    List<File> accepted = new ArrayList<File>();
    for (File file : files) {
      if (filter.accept(file)) {
        accepted.add(file);
      }
    }
    return accepted.toArray(new File[accepted.size()]);
  }

  private static String[] asNames(File[] files) {
    if (files == null) {
      return null;
    }
    List<String> names = new ArrayList<String>();
    for (File file : files) {
      names.add(file.getName());
    }
    return names.toArray(new String[0]);
  }
}
