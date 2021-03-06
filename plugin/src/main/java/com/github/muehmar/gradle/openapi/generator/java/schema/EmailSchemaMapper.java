package com.github.muehmar.gradle.openapi.generator.java.schema;

import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import io.swagger.v3.oas.models.media.EmailSchema;

public class EmailSchemaMapper extends SimpleSchemaMapper<EmailSchema> {
  public EmailSchemaMapper(JavaSchemaMapper nextMapper) {
    super(EmailSchema.class, JavaTypes.STRING, nextMapper);
  }
}
