package com.github.muehmar.gradle.openapi.generator.java.model;

import static com.github.muehmar.gradle.openapi.generator.model.name.PojoNames.pojoName;

public class JavaPojoNames {
  private JavaPojoNames() {}

  public static JavaPojoName invoiceName() {
    return JavaPojoName.wrap(pojoName("Invoice", "Dto"));
  }
}
