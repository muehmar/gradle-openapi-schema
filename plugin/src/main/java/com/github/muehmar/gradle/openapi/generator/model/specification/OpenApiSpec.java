package com.github.muehmar.gradle.openapi.generator.model.specification;

import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class OpenApiSpec {
  private final Path spec;

  private OpenApiSpec(Path spec) {
    this.spec = spec;
  }

  public static OpenApiSpec fromString(String spec) {
    return new OpenApiSpec(Paths.get(spec));
  }

  public static OpenApiSpec fromPath(Path spec) {
    return new OpenApiSpec(spec);
  }

  public Path asPathWithMainDirectory(MainDirectory mainDirectory) {
    return mainDirectory.asPath().resolve(spec);
  }
}
