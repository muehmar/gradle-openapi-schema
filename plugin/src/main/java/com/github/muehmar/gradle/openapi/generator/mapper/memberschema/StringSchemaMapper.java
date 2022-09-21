package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.ConstraintsMapper;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.util.Optionals;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Optional;

public class StringSchemaMapper extends BaseMemberSchemaMapper<StringSchema> {
  public StringSchemaMapper() {
    super(StringSchema.class);
  }

  @Override
  MemberSchemaMapResult mapSpecificSchema(
      PojoName pojoName,
      Name pojoMemberName,
      StringSchema schema,
      CompleteMemberSchemaMapper completeMapper) {
    final Constraints patternConstraints = ConstraintsMapper.getPattern(schema);
    final Constraints minAndMaxLengthConstraints = ConstraintsMapper.getMinAndMaxLength(schema);

    final Optional<Type> stringTypeWithFormat =
        Optional.ofNullable(schema.getFormat())
            .map(
                formatValue -> {
                  final StringType.Format format = StringType.Format.parseString(formatValue);
                  return StringType.ofFormatAndValue(format, formatValue);
                });

    final Optional<Type> enumType =
        Optional.ofNullable(schema.getEnum())
            .map(PList::fromIter)
            .map(
                members ->
                    EnumType.ofNameAndMembers(
                        pojoMemberName.startUpperCase().append("Enum"), members));

    final StringType rawStringType =
        StringType.ofFormat(StringType.Format.NONE)
            .withConstraints(patternConstraints.and(minAndMaxLengthConstraints));

    final Type type = Optionals.or(stringTypeWithFormat, enumType).orElse(rawStringType);

    return MemberSchemaMapResult.ofType(type);
  }
}
