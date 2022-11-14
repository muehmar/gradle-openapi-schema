package com.github.muehmar.gradle.openapi.generator.java.generator.shared;

import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;

public class PackageGenerator<T> implements Generator<T, PojoSettings> {
  @Override
  public Writer generate(T data, PojoSettings settings, Writer writer) {
    return writer.println("package %s;", settings.getPackageName());
  }
}
