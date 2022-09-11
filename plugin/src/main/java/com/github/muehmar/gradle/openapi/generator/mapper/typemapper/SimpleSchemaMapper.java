package com.github.muehmar.gradle.openapi.generator.mapper.typemapper;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import io.swagger.v3.oas.models.media.Schema;

abstract class SimpleSchemaMapper<T extends Schema<?>> extends BaseTypeMapper<T> {
  private final Type type;

  SimpleSchemaMapper(Class<T> schemaClass, Type type) {
    super(schemaClass);
    this.type = type;
  }

  @Override
  TypeMapResult mapSpecificSchema(
      PojoName pojoName, Name pojoMemberName, T schema, CompleteTypeMapper completeMapper) {
    return TypeMapResult.ofType(type);
  }
}
