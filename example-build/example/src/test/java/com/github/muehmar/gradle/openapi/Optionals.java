package com.github.muehmar.gradle.openapi;

import java.util.Optional;

public class Optionals {
  private Optionals() {}

  public static <T> Optional<T> opt(T value) {
    return Optional.of(value);
  }
}
