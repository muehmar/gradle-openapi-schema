package com.github.muehmar.gradle.openapi.generator.mapper.typemapper;

import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.java.schema.ConstraintsMapper;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import io.swagger.v3.oas.models.media.IntegerSchema;
import java.util.Optional;

public class IntegerSchemaMapper extends BaseTypeMapper<IntegerSchema> {
  IntegerSchemaMapper() {
    super(IntegerSchema.class);
  }

  @Override
  TypeMapResult mapSpecificSchema(
      PojoName pojoName,
      Name pojoMemberName,
      IntegerSchema schema,
      CompleteTypeMapper completeMapper) {
    final Constraints constraints = ConstraintsMapper.getMinimumAndMaximum(schema);
    final NumericType.Format format =
        Optional.ofNullable(schema.getFormat())
            .flatMap(NumericType.Format::parseString)
            .orElse(NumericType.Format.INTEGER);

    final NumericType numericType = NumericType.ofFormat(format).withConstraints(constraints);

    return TypeMapResult.ofType(numericType);
  }
}
