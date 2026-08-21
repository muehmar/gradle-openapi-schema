package com.github.muehmar.gradle.openapi.generator.java.model.name;

import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class JavaPojoName {
  private final Name name;
  private final String suffix;

  private JavaPojoName(Name name, String suffix) {
    this.name = name;
    this.suffix = suffix;
  }

  public static JavaPojoName fromPojoName(PojoName pojoName) {
    return new JavaPojoName(pojoName.getName(), pojoName.getSuffix());
  }

  public JavaPojoName append(JavaPojoName next) {
    return new JavaPojoName(this.name.append(next.name), this.suffix);
  }

  public PojoName asPojoName() {
    return PojoName.ofNameAndSuffix(name, suffix);
  }

  public JavaPojoName appendToName(String append) {
    return new JavaPojoName(name.append(append), suffix);
  }

  public JavaName asJavaName() {
    return JavaName.fromName(name.append(suffix));
  }

  public String asString() {
    return asJavaName().asString();
  }

  @Override
  public String toString() {
    return asString();
  }
}
