package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import com.github.muehmar.gradle.openapi.generator.mapper.ConstraintsMapper;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.EmailSchema;

public class EmailSchemaMapper extends BaseMemberSchemaMapper<EmailSchema> {
  EmailSchemaMapper() {
    super(EmailSchema.class);
  }

  @Override
  MemberSchemaMapResult mapSpecificSchema(
      PojoName pojoName,
      Name pojoMemberName,
      EmailSchema schema,
      CompleteMemberSchemaMapper completeMapper) {
    final Constraints patternConstraints = ConstraintsMapper.getPattern(schema);
    final Constraints minAndMaxLengthConstraints = ConstraintsMapper.getMinAndMaxLength(schema);
    final Constraints constraints =
        Constraints.ofEmail().and(patternConstraints).and(minAndMaxLengthConstraints);

    final StringType stringType =
        StringType.ofFormat(StringType.Format.EMAIL).withConstraints(constraints);

    return MemberSchemaMapResult.ofType(stringType);
  }
}
