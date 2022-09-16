package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import io.swagger.v3.oas.models.media.Schema;

abstract class SimpleSchemaMapper<T extends Schema<?>> extends BaseMemberSchemaMapper<T> {
  private final Type type;

  SimpleSchemaMapper(Class<T> schemaClass, Type type) {
    super(schemaClass);
    this.type = type;
  }

  @Override
  MemberSchemaMapResult mapSpecificSchema(
      PojoName pojoName, Name pojoMemberName, T schema, CompleteMemberSchemaMapper completeMapper) {
    return MemberSchemaMapResult.ofType(type);
  }
}
