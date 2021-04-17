package com.github.muehmar.gradle.openapi.generator.java.schema;

import com.github.muehmar.gradle.openapi.generator.data.MappedSchema;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.ComposedSchema;

public class ComposedSchemaMapper extends BaseSchemaMapper<ComposedSchema> {

  public ComposedSchemaMapper(JavaSchemaMapper nextMapper) {
    super(ComposedSchema.class, nextMapper);
  }

  @Override
  MappedSchema<JavaType> mapSpecificSchema(
      String pojoKey,
      String key,
      ComposedSchema schema,
      PojoSettings pojoSettings,
      JavaSchemaMapper chain) {
    if (schema.getOneOf() != null) {
      throw new IllegalArgumentException("oneOf composition is currently not supported");
    }

    if (schema.getAnyOf() != null) {
      throw new IllegalArgumentException("anyOf composition is currently not supported");
    }

    if (schema.getAllOf() == null) {
      throw new IllegalArgumentException("Schema composition does not contain any schemas.");
    }

    throw new UnsupportedOperationException("Not implemented yet");
  }
}
