package com.github.muehmar.gradle.openapi.generator.java.schema;

import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.Schema;

/**
 * Last mapper of a chain, throwing an exception as now mapper supported the given {@link Schema}.
 */
public class ThrowingSchemaMapper implements JavaSchemaMapper {
  @Override
  public JavaType mapSchema(PojoSettings pojoSettings, Schema<?> schema, JavaSchemaMapper chain) {
    throw new IllegalArgumentException("Not supported schema " + schema.getClass());
  }
}
