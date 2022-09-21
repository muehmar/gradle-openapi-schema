package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.NoType;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;

public class MemberSchemaLessSchemaMapper implements MemberSchemaMapper {
  @Override
  public Optional<MemberSchemaMapResult> map(
      PojoName pojoName,
      Name pojoMemberName,
      Schema<?> schema,
      CompleteMemberSchemaMapper completeMapper) {
    if (schema.getType() == null && schema.getFormat() == null) {
      final NoType noType = NoType.create();
      final MemberSchemaMapResult memberSchemaMapResult = MemberSchemaMapResult.ofType(noType);
      return Optional.of(memberSchemaMapResult);
    }
    return Optional.empty();
  }
}
