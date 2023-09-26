package com.github.muehmar.gradle.openapi.generator.java.model;

import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class JavaPojoName {
  private final String name;
  private final String suffix;

  private JavaPojoName(String name, String suffix) {
    this.name = name;
    this.suffix = suffix;
  }

  public static JavaPojoName wrap(PojoName pojoName) {
    return new JavaPojoName(pojoName.getName().asString(), pojoName.getSuffix());
  }

  public static JavaPojoName fromNameAndSuffix(String name, String suffix) {
    return new JavaPojoName(name, suffix);
  }

  public PojoName asPojoName() {
    return PojoName.ofNameAndSuffix(name, suffix);
  }

  public JavaPojoName appendToName(String append) {
    return new JavaPojoName(name.concat(append), suffix);
  }

  public JavaName asJavaName() {
    return JavaName.fromName(Name.ofString(asString()));
  }

  public JavaIdentifier asIdentifier() {
    return asJavaName().asIdentifier();
  }

  public String asString() {
    return name.concat(suffix);
  }

  @Override
  public String toString() {
    return asString();
  }
}
