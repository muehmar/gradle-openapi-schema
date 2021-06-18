package com.github.muehmar.gradle.openapi.generator.java;

public class JavaEscaper {
  private JavaEscaper() {}

  public static String escape(String value) {
    return value
        .replace("\\", "\\\\")
        .replace("\t", "\\t")
        .replace("\b", "\\b")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\f", "\\f")
        .replace("\"", "\\\"");
  }
}
