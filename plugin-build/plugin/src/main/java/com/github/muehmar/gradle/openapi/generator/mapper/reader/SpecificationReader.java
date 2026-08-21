package com.github.muehmar.gradle.openapi.generator.mapper.reader;

import com.github.muehmar.gradle.openapi.generator.model.specification.MainDirectory;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;

public interface SpecificationReader {
  String read(MainDirectory mainDirectory, OpenApiSpec specification);
}
