package com.github.muehmar.gradle.openapi.writer;

/** Can be used as {@link Writer} in tests, where the output can be fetched as string. */
public class TestStringWriter extends BaseWriter {

  public String asString() {
    return sb.toString();
  }

  @Override
  public boolean close(String path) {
    return true;
  }
}
