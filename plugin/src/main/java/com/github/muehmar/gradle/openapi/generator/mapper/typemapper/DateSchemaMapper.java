package com.github.muehmar.gradle.openapi.generator.mapper.typemapper;

import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.DateSchema;

public class DateSchemaMapper extends SimpleSchemaMapper<DateSchema> {
  DateSchemaMapper() {
    super(DateSchema.class, StringType.ofFormat(StringType.Format.DATE));
  }
}
