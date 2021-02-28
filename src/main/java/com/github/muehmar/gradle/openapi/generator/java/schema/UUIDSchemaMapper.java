package com.github.muehmar.gradle.openapi.generator.java.schema;

import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import io.swagger.v3.oas.models.media.UUIDSchema;

public class UUIDSchemaMapper extends SimpleSchemaMapper<UUIDSchema> {
  public UUIDSchemaMapper(JavaSchemaMapper nextMapper) {
    super(UUIDSchema.class, JavaTypes.UUID, nextMapper);
  }
}
