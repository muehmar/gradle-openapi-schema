package com.github.muehmar.gradle.openapi.generator.model.specification;

import java.nio.file.Path;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class OpenApiSpec {
  private final Path spec;

  private OpenApiSpec(Path spec) {
    this.spec = spec;
  }

  public static OpenApiSpec fromString(OpenApiSpec currentSpec, String spec) {
    final Path specPath =
        Optional.ofNullable(currentSpec.spec.getParent())
            .map(dir -> dir.resolve(spec))
            .orElse(Path.of(spec))
            .normalize();
    return new OpenApiSpec(specPath);
  }

  public static OpenApiSpec fromPath(Path spec) {
    return new OpenApiSpec(spec);
  }

  public Path asPathWithMainDirectory(MainDirectory mainDirectory) {
    return mainDirectory.asPath().resolve(spec);
  }
}
