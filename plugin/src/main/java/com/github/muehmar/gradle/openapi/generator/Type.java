package com.github.muehmar.gradle.openapi.generator;

import ch.bluecare.commons.data.PList;
import java.util.function.Consumer;

public interface Type {
  String getName();

  boolean isEnum();

  /**
   * Returns true in case this type contains a pojo as generic type or is a pojo itself. False in
   * case its a simple type like {@link String} or similar. It may sometimes return true although
   * the type is not really a pojo, as the plugin allows to use user defined types for which we have
   * no info what kind of type it is.
   */
  boolean containsPojo();

  /**
   * The provided {@code code} is executed in case this type is an enum with the list of members in
   * the enum as arguments.
   */
  void onEnum(Consumer<PList<String>> code);

  PList<String> getEnumMembers();

  PList<String> getImports();
}
