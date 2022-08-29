package com.github.muehmar.gradle.openapi.generator.mapper.typemapper;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;

abstract class BaseTypeMapper<T extends Schema<?>> implements TypeMapper {
  private final Class<T> schemaClass;

  BaseTypeMapper(Class<T> schemaClass) {
    this.schemaClass = schemaClass;
  }

  @Override
  public Optional<TypeMapResult> map(
      PojoName pojoName, Name pojoMemberName, Schema<?> schema, TypeMapper completeMapper) {
    if (schema.getClass().equals(schemaClass)) {
      return Optional.of(
          mapSpecificSchema(pojoName, pojoMemberName, schemaClass.cast(schema), completeMapper));
    }
    return Optional.empty();
  }

  abstract TypeMapResult mapSpecificSchema(
      PojoName pojoName, Name pojoMemberName, T schema, TypeMapper completeMapper);
}
