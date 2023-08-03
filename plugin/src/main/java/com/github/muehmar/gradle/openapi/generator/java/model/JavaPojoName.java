package com.github.muehmar.gradle.openapi.generator.java.model;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class JavaPojoName {
  private final PojoName pojoName;

  private JavaPojoName(PojoName pojoName) {
    this.pojoName = pojoName;
  }

  public static JavaPojoName wrap(PojoName pojoName) {
    return new JavaPojoName(pojoName);
  }

  public Name getSchemaName() {
    return pojoName.getName();
  }

  public JavaPojoName appendToName(String append) {
    return new JavaPojoName(pojoName.appendToName(append));
  }

  public JavaName asJavaName() {
    return JavaName.fromName(Name.ofString(asString()));
  }

  public JavaIdentifier asIdentifier() {
    return asJavaName().asIdentifier();
  }

  public String asString() {
    return pojoName.asString();
  }

  @Override
  public String toString() {
    return asString();
  }
}
