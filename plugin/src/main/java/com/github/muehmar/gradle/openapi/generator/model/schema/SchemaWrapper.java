package com.github.muehmar.gradle.openapi.generator.model.schema;

import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import io.swagger.v3.oas.models.media.Schema;
import lombok.Value;

@Value
public class SchemaWrapper {
  OpenApiSpec spec;
  Schema<?> schema;
}
