package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;

abstract class BaseMemberSchemaMapper<T extends Schema<?>> implements MemberSchemaMapper {
  private final Class<T> schemaClass;

  BaseMemberSchemaMapper(Class<T> schemaClass) {
    this.schemaClass = schemaClass;
  }

  @Override
  public Optional<MemberSchemaMapResult> map(
      PojoName pojoName,
      Name pojoMemberName,
      Schema<?> schema,
      CompleteMemberSchemaMapper completeMapper) {
    if (schema.getClass().equals(schemaClass)) {
      return Optional.of(
          mapSpecificSchema(pojoName, pojoMemberName, schemaClass.cast(schema), completeMapper));
    }
    return Optional.empty();
  }

  abstract MemberSchemaMapResult mapSpecificSchema(
      PojoName pojoName, Name pojoMemberName, T schema, CompleteMemberSchemaMapper completeMapper);
}
