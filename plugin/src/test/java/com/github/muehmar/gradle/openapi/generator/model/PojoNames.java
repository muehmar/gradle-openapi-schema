package com.github.muehmar.gradle.openapi.generator.model;

public class PojoNames {
  private PojoNames() {}

  public static final PojoName POJO_NAME = PojoName.ofName(Name.ofString("pojoName"));

  public static PojoName pojoName(String name, String schema, String suffix) {
    return new PojoName(Name.ofString(name), SchemaName.ofString(schema), suffix);
  }
}
