package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import com.github.muehmar.gradle.openapi.generator.model.type.BooleanType;
import io.swagger.v3.oas.models.media.BooleanSchema;

public class BooleanSchemaMapper extends SimpleSchemaMapper<BooleanSchema> {
  BooleanSchemaMapper() {
    super(BooleanSchema.class, BooleanType.create());
  }
}
