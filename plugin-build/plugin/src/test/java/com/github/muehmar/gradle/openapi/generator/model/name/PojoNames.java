package com.github.muehmar.gradle.openapi.generator.model.name;

public class PojoNames {
  private PojoNames() {}

  public static final PojoName POJO_NAME = PojoName.ofName(Name.ofString("PojoName"));

  public static PojoName pojoName(String name, String suffix) {
    return new PojoName(Name.ofString(name), suffix);
  }
}
