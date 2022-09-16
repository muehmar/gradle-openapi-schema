package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import com.github.muehmar.gradle.openapi.generator.mapper.ConstraintsMapper;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.PasswordSchema;

public class PasswordSchemaMapper extends BaseMemberSchemaMapper<PasswordSchema> {
  PasswordSchemaMapper() {
    super(PasswordSchema.class);
  }

  @Override
  MemberSchemaMapResult mapSpecificSchema(
      PojoName pojoName,
      Name pojoMemberName,
      PasswordSchema schema,
      CompleteMemberSchemaMapper completeMapper) {
    final Constraints patternConstraints = ConstraintsMapper.getPattern(schema);
    final Constraints minAndMaxLengthConstraints = ConstraintsMapper.getMinAndMaxLength(schema);

    final StringType passwordType =
        StringType.ofFormat(StringType.Format.PASSWORD)
            .withConstraints(patternConstraints.and(minAndMaxLengthConstraints));
    return MemberSchemaMapResult.ofType(passwordType);
  }
}
