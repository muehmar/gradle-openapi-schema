package com.github.muehmar.gradle.openapi.generator.model.specification;

import java.nio.file.Path;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class MainDirectory {
  private final Path dir;

  private MainDirectory(Path dir) {
    this.dir = dir;
  }

  public static MainDirectory fromString(String dir) {
    return new MainDirectory(Path.of(dir));
  }

  public static MainDirectory fromPath(Path dir) {
    return new MainDirectory(dir);
  }

  public Path asPath() {
    return dir;
  }
}
