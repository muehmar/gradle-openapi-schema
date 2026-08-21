package com.github.muehmar.gradle.openapi.generator.mapper.reader;

import com.github.muehmar.gradle.openapi.Resources;
import com.github.muehmar.gradle.openapi.generator.model.specification.MainDirectory;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;

public class ResourceSpecificationReader implements SpecificationReader {
  @Override
  public String read(MainDirectory mainDirectory, OpenApiSpec specification) {
    return Resources.readString(specification.asPathWithMainDirectory(mainDirectory).toString());
  }
}
