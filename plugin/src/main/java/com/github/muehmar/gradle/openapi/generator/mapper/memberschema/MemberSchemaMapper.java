package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.util.Optionals;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;

public interface MemberSchemaMapper {
  Optional<MemberSchemaMapResult> map(
      PojoName pojoName,
      Name pojoMemberName,
      Schema<?> schema,
      CompleteMemberSchemaMapper completeMapper);

  default MemberSchemaMapper or(MemberSchemaMapper next) {
    final MemberSchemaMapper self = this;
    return ((pojoName, pojoMemberName, schema, completeMapper) ->
        Optionals.or(
            () -> self.map(pojoName, pojoMemberName, schema, completeMapper),
            () -> next.map(pojoName, pojoMemberName, schema, completeMapper)));
  }
}
