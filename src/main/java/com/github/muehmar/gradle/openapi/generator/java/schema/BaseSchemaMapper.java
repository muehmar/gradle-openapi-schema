package com.github.muehmar.gradle.openapi.generator.java.schema;

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

  public BaseSchemaMapper(Class<T> schemaClass, JavaSchemaMapper nextMapper) {
    this.schemaClass = schemaClass;
    this.nextMapper = nextMapper;
  }

  @Override
  public JavaType mapSchema(PojoSettings pojoSettings, Schema<?> schema, JavaSchemaMapper chain) {
    if (schema.getClass().equals(schemaClass)) {
      return mapSpecificSchema(pojoSettings, schemaClass.cast(schema), chain);
    }
    return nextMapper.mapSchema(pojoSettings, schema, chain);
  }

  /**
   * Is called in case the given {@link Schema} is an instance of the supported schema {@link T}.
   */
  abstract JavaType mapSpecificSchema(PojoSettings pojoSettings, T schema, JavaSchemaMapper chain);
}
