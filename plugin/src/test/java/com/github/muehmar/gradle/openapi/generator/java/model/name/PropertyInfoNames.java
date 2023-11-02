package com.github.muehmar.gradle.openapi.generator.java.model.name;

public class PropertyInfoNames {
  private PropertyInfoNames() {}

  public static PropertyInfoName invoiceNumber() {
    return PropertyInfoName.fromPojoNameAndMemberName(
        JavaPojoNames.invoiceName(), JavaName.fromString("number"));
  }
}
