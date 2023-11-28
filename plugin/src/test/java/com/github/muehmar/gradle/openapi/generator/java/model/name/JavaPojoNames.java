package com.github.muehmar.gradle.openapi.generator.java.model.name;

import static com.github.muehmar.gradle.openapi.generator.model.name.PojoNames.pojoName;

import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;

public class JavaPojoNames {
  private JavaPojoNames() {}

  public static JavaPojoName invoiceName() {
    return JavaPojoName.fromPojoName(pojoName("Invoice", "Dto"));
  }

  public static JavaPojoName patientName() {
    return JavaPojoName.fromPojoName(pojoName("Patient", "Dto"));
  }

  public static JavaPojoName fromNameAndSuffix(String name, String suffix) {
    return JavaPojoName.fromPojoName(PojoName.ofNameAndSuffix(name, suffix));
  }
}
