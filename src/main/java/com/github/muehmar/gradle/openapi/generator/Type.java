package com.github.muehmar.gradle.openapi.generator;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public interface Type {
  String getName();

  boolean isEnum();

  default boolean notEnum() {
    return !isEnum();
  }

  /**
   * The provided {@code code} is executed in case this type is an enum with the list of members in
   * the enum as arguments.
   */
  void onEnum(Consumer<List<String>> code);

  Set<String> getImports();
}
