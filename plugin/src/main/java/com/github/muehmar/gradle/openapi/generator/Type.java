package com.github.muehmar.gradle.openapi.generator;

import ch.bluecare.commons.data.PList;
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
  void onEnum(Consumer<PList<String>> code);

  PList<String> getEnumMembers();

  PList<String> getImports();
}
