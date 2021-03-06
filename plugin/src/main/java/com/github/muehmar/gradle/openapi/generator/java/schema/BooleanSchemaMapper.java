package com.github.muehmar.gradle.openapi.generator.java.schema;

import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import io.swagger.v3.oas.models.media.BooleanSchema;

public class BooleanSchemaMapper extends SimpleSchemaMapper<BooleanSchema> {
  public BooleanSchemaMapper(JavaSchemaMapper nextMapper) {
    super(BooleanSchema.class, JavaTypes.BOOLEAN, nextMapper);
  }
}
