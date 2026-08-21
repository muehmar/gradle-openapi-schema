package com.github.muehmar.gradle.openapi.exception;

public class OpenApiGeneratorException extends RuntimeException {
  public OpenApiGeneratorException(String message) {
    super(message);
  }

  public OpenApiGeneratorException(String format, Object... args) {
    super(String.format(format, args));
  }
}
