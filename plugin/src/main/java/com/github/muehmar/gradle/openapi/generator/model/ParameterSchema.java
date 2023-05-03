package com.github.muehmar.gradle.openapi.generator.model;

import com.github.muehmar.gradle.openapi.generator.model.schema.OpenApiSchema;
import io.swagger.v3.oas.models.media.Schema;
import lombok.Value;

@Value
public class ParameterSchema {
  Name name;
  OpenApiSchema schema;

  public ParameterSchema(Name name, OpenApiSchema schema) {
    this.name = name;
    this.schema = schema;
  }

  public ParameterSchema(Name name, Schema<?> schema) {
    this(name, OpenApiSchema.wrapSchema(schema));
  }
}
