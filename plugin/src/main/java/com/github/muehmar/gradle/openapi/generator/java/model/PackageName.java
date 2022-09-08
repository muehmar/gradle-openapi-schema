package com.github.muehmar.gradle.openapi.generator.java.model;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class PackageName {
  private final Name pkg;

  private PackageName(Name pkg) {
    this.pkg = pkg;
  }

  public static PackageName ofString(String pkg) {
    return new PackageName(Name.ofString(pkg));
  }

  public Name qualifiedClassName(Name className) {
    return pkg.append(".").append(className);
  }
}
