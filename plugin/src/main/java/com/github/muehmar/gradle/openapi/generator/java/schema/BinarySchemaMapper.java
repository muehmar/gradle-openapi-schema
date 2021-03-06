package com.github.muehmar.gradle.openapi.generator.java.schema;

import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import io.swagger.v3.oas.models.media.BinarySchema;

public class BinarySchemaMapper extends SimpleSchemaMapper<BinarySchema> {
  public BinarySchemaMapper(JavaSchemaMapper nextMapper) {
    super(BinarySchema.class, JavaTypes.BYTE_ARRAY, nextMapper);
  }
}
