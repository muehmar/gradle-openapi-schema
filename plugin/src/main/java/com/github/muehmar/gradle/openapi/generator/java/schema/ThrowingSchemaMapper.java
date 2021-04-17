package com.github.muehmar.gradle.openapi.generator.java.schema;

import com.github.muehmar.gradle.openapi.generator.data.MappedSchema;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.Schema;

/**
 * Last mapper of a chain, throwing an exception as now mapper supported the given {@link Schema}.
 */
public class ThrowingSchemaMapper implements JavaSchemaMapper {
  @Override
  public MappedSchema<JavaType> mapSchema(
      String pojoKey,
      String key,
      Schema<?> schema,
      PojoSettings pojoSettings,
      JavaSchemaMapper chain) {
    throw new IllegalArgumentException(
        "Not supported schema for pojo key " + pojoKey + " key " + key + ": " + schema.toString());
  }
}
