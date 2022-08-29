package com.github.muehmar.gradle.openapi.generator.mapper.typemapper;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.NewType;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import io.swagger.v3.oas.models.media.Schema;

abstract class SimpleSchemaMapper<T extends Schema<?>> extends BaseTypeMapper<T> {
  private final NewType type;

  SimpleSchemaMapper(Class<T> schemaClass, NewType type) {
    super(schemaClass);
    this.type = type;
  }

  @Override
  TypeMapResult mapSpecificSchema(
      PojoName pojoName, Name pojoMemberName, T schema, TypeMapper completeMapper) {
    return TypeMapResult.ofType(type);
  }
}
