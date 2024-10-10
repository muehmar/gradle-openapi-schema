package com.github.muehmar.gradle.openapi.generator.settings;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class PackageName implements Serializable {
  private final String name;

  private PackageName(String name) {
    this.name = name;
  }

  public static PackageName fromString(String packageName) {
    return new PackageName(packageName);
  }

  public String getName() {
    return name;
  }

  public Path asPath() {
    return Paths.get(name.replace(".", File.separator).replaceFirst("^" + File.separator, ""));
  }

  @Override
  public String toString() {
    return name;
  }
}
