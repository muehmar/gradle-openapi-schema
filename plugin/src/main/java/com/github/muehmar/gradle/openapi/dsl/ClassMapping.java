package com.github.muehmar.gradle.openapi.dsl;

import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import java.io.Serializable;
import java.util.Objects;

public class ClassMapping implements Serializable {
  private String fromClass;
  private String toClass;
  private String imports;

  public void setFromClass(String fromClass) {
    this.fromClass = fromClass;
  }

  public void setToClass(String toClass) {
    this.toClass = toClass;
  }

  public void setImports(String imports) {
    this.imports = imports;
  }

  public ClassTypeMapping toSettingsClassMapping() {
    return new ClassTypeMapping(fromClass, toClass, imports);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ClassMapping that = (ClassMapping) o;
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
    return "ClassMapping{"
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
