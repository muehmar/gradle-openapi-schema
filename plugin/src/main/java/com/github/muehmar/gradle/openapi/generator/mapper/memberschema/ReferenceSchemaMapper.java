package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;

public class ReferenceSchemaMapper implements MemberSchemaMapper {
  @Override
  public Optional<MemberSchemaMapResult> map(
      PojoName pojoName,
      Name pojoMemberName,
      Schema<?> schema,
      CompleteMemberSchemaMapper completeMapper) {
    if (schema.getType() == null && schema.getFormat() == null && schema.get$ref() != null) {
      final PojoName name = ReferenceMapper.getPojoName(schema.get$ref(), pojoName.getSuffix());
      final ObjectType objectType = ObjectType.ofName(name);
      return Optional.of(MemberSchemaMapResult.ofType(objectType));
    }
    return Optional.empty();
  }
}
