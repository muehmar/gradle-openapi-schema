package com.github.muehmar.gradle.openapi.generator.java.model;

import com.github.muehmar.gradle.openapi.generator.java.generator.parameter.JavaParameter;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class JavaFileName {
  private final Path path;

  private JavaFileName(Path path) {
    this.path = path;
  }

  public static JavaFileName fromRef(String ref) {
    return new JavaFileName(Paths.get(ref.replace(".", File.separator) + ".java"));
  }

  public static JavaFileName fromSettingsAndPojo(PojoSettings settings, JavaPojo pojo) {
    return fromSettingsAndClassname(settings, pojo.getClassName());
  }

  public static JavaFileName fromSettingsAndClassname(PojoSettings settings, JavaName className) {
    final Path packagePath = settings.getPackageName().asPath();
    final String fileNameString =
        String.format("%s%s%s.java", packagePath.toFile(), File.separator, className);
    return new JavaFileName(Paths.get(fileNameString));
  }

  public static JavaFileName fromSettingsAndParameter(
      PojoSettings settings, JavaParameter parameter) {
    final Path packagePath = settings.getPackageName().asPath();
    final String fileNameString =
        String.format(
            "%s%sparameter%s%s.java",
            packagePath.toFile(), File.separator, File.separator, parameter.getParamClassName());
    return new JavaFileName(Paths.get(fileNameString));
  }

  public Path asPath() {
    return path;
  }
}
