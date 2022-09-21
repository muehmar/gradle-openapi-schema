package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.DateTimeSchema;

public class DateTimeSchemaMapper extends SimpleSchemaMapper<DateTimeSchema> {
  DateTimeSchemaMapper() {
    super(DateTimeSchema.class, StringType.ofFormat(StringType.Format.DATE_TIME));
  }
}
