package com.github.muehmar.gradle.openapi.generator.java.schema;

import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import io.swagger.v3.oas.models.media.DateTimeSchema;

public class DateTimeSchemaMapper extends SimpleSchemaMapper<DateTimeSchema> {
  public DateTimeSchemaMapper(JavaSchemaMapper nextMapper) {
    super(DateTimeSchema.class, JavaTypes.LOCAL_DATE_TIME, nextMapper);
  }
}
