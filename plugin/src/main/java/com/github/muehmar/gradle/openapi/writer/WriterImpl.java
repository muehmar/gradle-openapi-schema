package com.github.muehmar.gradle.openapi.writer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.IntStream;

public class WriterImpl implements Writer {
  private static final int DEFAULT_SPACES_PER_TAB = 2;
  private static final String NEWLINE_STRING = "\n";

  private final String tab;
  private final String basePath;
  private final StringBuilder sb;

  private int tabs;
  private boolean newline;

  public WriterImpl(String basePath) {
    this(basePath, DEFAULT_SPACES_PER_TAB);
  }

  public WriterImpl(String basePath, int spacesPerIndent) {
    this.basePath = basePath;
    this.tab = new String(new char[spacesPerIndent]).replace("\0", " ");
    this.sb = new StringBuilder();

    this.tabs = 0;
    this.newline = true;
  }

  @Override
  public Writer print(String string, Object... args) {
    if (newline) {
      printTabs();
      newline = false;
    }
    sb.append(String.format(string, args));
    return this;
  }

  private void printTabs() {
    IntStream.range(0, tabs).forEach(i -> sb.append(tab));
  }

  @Override
  public Writer println() {
    sb.append(NEWLINE_STRING);
    tabs = 0;
    newline = true;
    return this;
  }

  @Override
  public Writer tab(int tabs) {
    this.tabs = tabs;
    return this;
  }

  @Override
  public Writer ref(String ref) {
    return this;
  }

  @Override
  public boolean close(String subPath) {
    try {
      final Path file = Paths.get(basePath).resolve(subPath);
      file.toFile().mkdirs();

      Files.delete(file);

      try (final PrintWriter writer =
          new PrintWriter(
              new OutputStreamWriter(
                  new FileOutputStream(file.toFile()), StandardCharsets.UTF_8))) {

        writer.append(sb.toString());
        writer.flush();
      }
      return false;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
