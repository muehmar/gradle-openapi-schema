package com.github.muehmar.gradle.openapi.generator.mapper.typemapper;

import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.java.schema.ConstraintsMapper;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.PasswordSchema;

public class PasswordSchemaMapper extends BaseTypeMapper<PasswordSchema> {
  PasswordSchemaMapper() {
    super(PasswordSchema.class);
  }

  @Override
  TypeMapResult mapSpecificSchema(
      PojoName pojoName,
      Name pojoMemberName,
      PasswordSchema schema,
      CompleteTypeMapper completeMapper) {
    final Constraints patternConstraints = ConstraintsMapper.getPattern(schema);
    final Constraints minAndMaxLengthConstraints = ConstraintsMapper.getMinAndMaxLength(schema);

    final StringType passwordType =
        StringType.ofFormat(StringType.Format.PASSWORD)
            .withConstraints(patternConstraints.and(minAndMaxLengthConstraints));
    return TypeMapResult.ofType(passwordType);
  }
}
