package com.github.muehmar.gradle.openapi.generator.model;

import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.schema.OpenApiSchema;
import com.github.muehmar.gradle.openapi.generator.model.schema.SchemaWrapper;
import lombok.Value;

@Value
public class ParameterSchema {
  Name name;
  OpenApiSchema schema;

  public ParameterSchema(Name name, OpenApiSchema schema) {
    this.name = name;
    this.schema = schema;
  }

  public ParameterSchema(Name name, SchemaWrapper wrapper) {
    this(name, OpenApiSchema.wrapSchema(wrapper));
  }
}
