package com.github.muehmar.gradle.openapi.generator.settings;

import com.github.muehmar.gradle.openapi.OpenApiSchemaGeneratorExtension;
import java.io.Serializable;
import java.util.Objects;

public class ClassTypeMapping implements Serializable {
  private final String fromClass;
  private final String toClass;
  private final String imports;

  public ClassTypeMapping(String fromClass, String toClass, String imports) {
    this.fromClass = fromClass;
    this.toClass = toClass;
    this.imports = imports;
  }

  public static ClassTypeMapping fromExtension(OpenApiSchemaGeneratorExtension.ClassMapping ext) {
    return new ClassTypeMapping(ext.getFromClass(), ext.getToClass(), ext.getImports());
  }

  public String getFromClass() {
    return fromClass;
  }

  public String getToClass() {
    return toClass;
  }

  public String getImports() {
    return imports;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ClassTypeMapping that = (ClassTypeMapping) o;
    return Objects.equals(fromClass, that.fromClass)
        && Objects.equals(toClass, that.toClass)
        && Objects.equals(imports, that.imports);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fromClass, toClass, imports);
  }

  @Override
  public String toString() {
    return "ClassTypeMapping{"
        + "fromClass='"
        + fromClass
        + '\''
        + ", toClass='"
        + toClass
        + '\''
        + ", imports='"
        + imports
        + '\''
        + '}';
  }
}
