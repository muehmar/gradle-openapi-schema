package com.github.muehmar.gradle.openapi.generator.java.schema;

import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import io.swagger.v3.oas.models.media.DateSchema;

public class DateSchemaMapper extends SimpleSchemaMapper<DateSchema> {
  public DateSchemaMapper(JavaSchemaMapper nextMapper) {
    super(DateSchema.class, JavaTypes.LOCAL_DATE, nextMapper);
  }
}
