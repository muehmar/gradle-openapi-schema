package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import io.swagger.v3.oas.models.media.Schema;

public interface CompleteMemberSchemaMapper {
  MemberSchemaMapResult map(PojoName pojoName, Name pojoMemberName, Schema<?> schema);
}
