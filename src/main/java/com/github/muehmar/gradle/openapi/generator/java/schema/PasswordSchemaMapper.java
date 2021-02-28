package com.github.muehmar.gradle.openapi.generator.java.schema;

import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import io.swagger.v3.oas.models.media.PasswordSchema;

public class PasswordSchemaMapper extends SimpleSchemaMapper<PasswordSchema> {
  public PasswordSchemaMapper(JavaSchemaMapper nextMapper) {
    super(PasswordSchema.class, JavaTypes.STRING, nextMapper);
  }
}
