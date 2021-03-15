package com.github.muehmar.gradle.openapi.writer;

import java.util.stream.IntStream;

/**
 * Base implementation of a {@link Writer}. Extending classes should implement the {@link
 * Writer#close(String)} method
 */
abstract class BaseWriter implements Writer {
  private static final int DEFAULT_SPACES_PER_TAB = 2;
  private static final String NEWLINE_STRING = "\n";

  private final String tab;

  /** Contains the complete content written with this Writer. */
  protected final StringBuilder sb;

  private int tabs;
  private boolean newline;

  protected BaseWriter() {
    this(DEFAULT_SPACES_PER_TAB);
  }

  protected BaseWriter(int spacesPerIndent) {
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
}
