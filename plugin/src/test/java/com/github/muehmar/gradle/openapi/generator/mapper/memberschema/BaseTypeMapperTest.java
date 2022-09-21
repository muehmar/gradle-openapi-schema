package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoNames;
import io.swagger.v3.oas.models.media.Schema;

abstract class BaseTypeMapperTest {
  private static final CompleteMemberSchemaMapper TYPE_MAPPER =
      CompleteMemberSchemaMapperFactory.create();

  protected MemberSchemaMapResult run(Schema<?> schema) {
    return TYPE_MAPPER.map(PojoNames.POJO_NAME, Name.ofString("pojoMemberName"), schema);
  }

  protected MemberSchemaMapResult run(PojoName pojoName, Name pojoMemberName, Schema<?> schema) {
    return TYPE_MAPPER.map(pojoName, pojoMemberName, schema);
  }
}
