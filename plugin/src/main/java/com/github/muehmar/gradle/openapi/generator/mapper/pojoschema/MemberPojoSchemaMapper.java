package com.github.muehmar.gradle.openapi.generator.mapper.pojoschema;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.memberschema.CompleteMemberSchemaMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.memberschema.CompleteMemberSchemaMapperFactory;
import com.github.muehmar.gradle.openapi.generator.mapper.memberschema.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoMemberReference;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Objects;
import java.util.Optional;

public class MemberPojoSchemaMapper implements SinglePojoSchemaMapper {
  private static final CompleteMemberSchemaMapper COMPLETE_TYPE_MAPPER =
      CompleteMemberSchemaMapperFactory.create();
  private static final PList<String> SUPPORTED_MEMBER_SCHEMAS =
      PList.of("string", "integer", "number", "boolean");

  @Override
  public Optional<PojoSchemaMapResult> map(
      PojoSchema pojoSchema, CompletePojoSchemaMapper completePojoSchemaMapper) {
    final String type = pojoSchema.getSchema().getType();
    if (Objects.nonNull(type) && SUPPORTED_MEMBER_SCHEMAS.exists(type::equals)) {
      return Optional.of(
          PojoSchemaMapResult.ofPojoMemberReference(
              processMemberSchema(pojoSchema.getPojoName(), pojoSchema.getSchema())));
    } else {
      return Optional.empty();
    }
  }

  private PojoMemberReference processMemberSchema(PojoName name, Schema<?> schema) {
    final MemberSchemaMapResult result =
        COMPLETE_TYPE_MAPPER.map(PojoName.ofName(Name.ofString("Unused")), name.getName(), schema);

    return new PojoMemberReference(name, schema.getDescription(), result.getType());
  }
}
