package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.wither;

import io.github.muehmar.codegenerator.writer.Writer;
import java.util.function.Function;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class StringOrWriter {
  private final String stringValue;
  private final Writer writerValue;

  private StringOrWriter(String stringValue, Writer writerValue) {
    this.stringValue = stringValue;
    this.writerValue = writerValue;
  }

  public static StringOrWriter ofString(String stringValue) {
    return new StringOrWriter(stringValue, null);
  }

  public static StringOrWriter ofWriter(Writer writerValue) {
    return new StringOrWriter(null, writerValue);
  }

  public <T> T fold(Function<String, T> onString, Function<Writer, T> onWriter) {
    if (stringValue != null) {
      return onString.apply(stringValue);
    } else {
      return onWriter.apply(writerValue);
    }
  }
}
