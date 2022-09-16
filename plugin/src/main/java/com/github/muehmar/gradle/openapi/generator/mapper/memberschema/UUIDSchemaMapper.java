package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.UUIDSchema;

public class UUIDSchemaMapper extends SimpleSchemaMapper<UUIDSchema> {
  UUIDSchemaMapper() {
    super(UUIDSchema.class, StringType.ofFormat(StringType.Format.UUID));
  }
}
