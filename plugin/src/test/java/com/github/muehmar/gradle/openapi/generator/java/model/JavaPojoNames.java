package com.github.muehmar.gradle.openapi.generator.java.model;

import com.github.muehmar.gradle.openapi.generator.model.PojoName;

public class JavaPojoNames {
  private JavaPojoNames() {}

  public static JavaPojoName invoiceName() {
    return JavaPojoName.wrap(PojoName.ofNameAndSuffix("Invoice", "Dto"));
  }
}
