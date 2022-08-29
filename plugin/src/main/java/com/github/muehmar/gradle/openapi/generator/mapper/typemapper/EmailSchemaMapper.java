package com.github.muehmar.gradle.openapi.generator.mapper.typemapper;

import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.java.schema.ConstraintsMapper;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.EmailSchema;

public class EmailSchemaMapper extends BaseTypeMapper<EmailSchema> {
  EmailSchemaMapper() {
    super(EmailSchema.class);
  }

  @Override
  TypeMapResult mapSpecificSchema(
      PojoName pojoName, Name pojoMemberName, EmailSchema schema, TypeMapper completeMapper) {
    final Constraints patternConstraints = ConstraintsMapper.getPattern(schema);
    final Constraints minAndMaxLengthConstraints = ConstraintsMapper.getMinAndMaxLength(schema);
    final Constraints constraints =
        Constraints.ofEmail().and(patternConstraints).and(minAndMaxLengthConstraints);

    final StringType stringType =
        StringType.ofFormat(StringType.Format.EMAIL).withConstraints(constraints);

    return TypeMapResult.ofType(stringType);
  }
}
