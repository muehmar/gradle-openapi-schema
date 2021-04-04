package com.github.muehmar.gradle.openapi.generator.java.schema;

import com.github.muehmar.gradle.openapi.generator.MappedSchema;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.Schema;

/**
 * Simple mapper where a given concrete type of {@link Schema} is mapped to one specific {@link
 * JavaType}.
 */
public class SimpleSchemaMapper<T extends Schema<?>> extends BaseSchemaMapper<T> {

  private final JavaType javaType;

  public SimpleSchemaMapper(Class<T> schemaClass, JavaType javaType, JavaSchemaMapper nextMapper) {
    super(schemaClass, nextMapper);
    this.javaType = javaType;
  }

  @Override
  MappedSchema<JavaType> mapSpecificSchema(
      String pojoKey, String key, T schema, PojoSettings pojoSettings, JavaSchemaMapper chain) {
    return MappedSchema.ofType(javaType);
  }
}