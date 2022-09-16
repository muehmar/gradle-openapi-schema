package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import static com.github.muehmar.gradle.openapi.generator.model.type.StringType.Format.BINARY;

import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.BinarySchema;

public class BinarySchemaMapper extends SimpleSchemaMapper<BinarySchema> {
  BinarySchemaMapper() {
    super(BinarySchema.class, StringType.ofFormat(BINARY));
  }
}
