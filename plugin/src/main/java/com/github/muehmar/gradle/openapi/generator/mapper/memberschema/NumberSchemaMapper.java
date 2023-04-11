package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import com.github.muehmar.gradle.openapi.generator.mapper.ConstraintsMapper;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import io.swagger.v3.oas.models.media.NumberSchema;
import java.util.Optional;

public class NumberSchemaMapper extends BaseMemberSchemaMapper<NumberSchema> {
  NumberSchemaMapper() {
    super(NumberSchema.class);
  }

  @Override
  MemberSchemaMapResult mapSpecificSchema(
      PojoName pojoName,
      Name pojoMemberName,
      NumberSchema schema,
      CompleteMemberSchemaMapper completeMapper) {
    final Constraints constraints =
        ConstraintsMapper.getDecimalMinimumAndMaximum(schema)
            .and(ConstraintsMapper.getMultipleOf(schema));
    final NumericType.Format format =
        Optional.ofNullable(schema.getFormat())
            .flatMap(NumericType.Format::parseString)
            .orElse(NumericType.Format.FLOAT);

    final NumericType numericType = NumericType.ofFormat(format).withConstraints(constraints);

    return MemberSchemaMapResult.ofType(numericType);
  }
}
