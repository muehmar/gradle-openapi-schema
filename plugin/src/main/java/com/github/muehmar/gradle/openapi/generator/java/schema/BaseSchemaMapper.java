package com.github.muehmar.gradle.openapi.generator.java.schema;

import com.github.muehmar.gradle.openapi.generator.MappedSchema;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.Schema;

/**
 * Base class for a {@link JavaSchemaMapper}, calling the actual implementation of the mapping in
 * case the given {@link Schema} is an instance of the supported schema {@link T} or forward the
 * schema to the next mapper down the chain.
 */
abstract class BaseSchemaMapper<T extends Schema<?>> implements JavaSchemaMapper {
  private final Class<T> schemaClass;
  private final JavaSchemaMapper nextMapper;

  protected BaseSchemaMapper(Class<T> schemaClass, JavaSchemaMapper nextMapper) {
    this.schemaClass = schemaClass;
    this.nextMapper = nextMapper;
  }

  @Override
  public MappedSchema<JavaType> mapSchema(
      String pojoKey,
      String key,
      Schema<?> schema,
      PojoSettings pojoSettings,
      JavaSchemaMapper chain) {
    if (schema.getClass().equals(schemaClass)) {
      return mapSpecificSchema(pojoKey, key, schemaClass.cast(schema), pojoSettings, chain);
    }
    return nextMapper.mapSchema(pojoKey, key, schema, pojoSettings, chain);
  }

  /**
   * Is called in case the given {@link Schema} is an instance of the supported schema {@link T}.
   */
  abstract MappedSchema<JavaType> mapSpecificSchema(
      String pojoKey, String key, T schema, PojoSettings pojoSettings, JavaSchemaMapper chain);
}
