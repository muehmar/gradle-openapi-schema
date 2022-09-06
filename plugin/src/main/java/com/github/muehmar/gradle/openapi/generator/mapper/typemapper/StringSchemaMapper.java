package com.github.muehmar.gradle.openapi.generator.mapper.typemapper;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.java.schema.ConstraintsMapper;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.NewType;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.util.Optionals;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Optional;

public class StringSchemaMapper extends BaseTypeMapper<StringSchema> {
  public StringSchemaMapper() {
    super(StringSchema.class);
  }

  @Override
  TypeMapResult mapSpecificSchema(
      PojoName pojoName,
      Name pojoMemberName,
      StringSchema schema,
      CompleteTypeMapper completeMapper) {
    final Constraints patternConstraints = ConstraintsMapper.getPattern(schema);
    final Constraints minAndMaxLengthConstraints = ConstraintsMapper.getMinAndMaxLength(schema);

    final Optional<NewType> stringTypeWithFormat =
        Optional.ofNullable(schema.getFormat())
            .map(
                formatValue -> {
                  final StringType.Format format = StringType.Format.parseString(formatValue);
                  return StringType.ofFormatAndValue(format, formatValue);
                });

    final Optional<NewType> enumType =
        Optional.ofNullable(schema.getEnum()).map(PList::fromIter).map(EnumType::ofMembers);

    final StringType rawStringType =
        StringType.ofFormat(StringType.Format.NONE)
            .withConstraints(patternConstraints.and(minAndMaxLengthConstraints));

    final NewType type = Optionals.or(stringTypeWithFormat, enumType).orElse(rawStringType);

    return TypeMapResult.ofType(type);
  }
}
