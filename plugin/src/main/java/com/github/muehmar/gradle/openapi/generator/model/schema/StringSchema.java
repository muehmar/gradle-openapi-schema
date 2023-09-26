package com.github.muehmar.gradle.openapi.generator.model.schema;

import com.github.muehmar.gradle.openapi.generator.mapper.ConstraintsMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.model.PojoMemberReference;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class StringSchema implements OpenApiSchema {
  private final Schema<?> delegate;
  private final Optional<String> format;

  private StringSchema(Schema<?> delegate, Optional<String> format) {
    this.delegate = delegate;
    this.format = format;
  }

  public static Optional<StringSchema> wrap(Schema<?> schema) {
    if (SchemaType.STRING.matchesType(schema) && schema.getEnum() == null) {
      final StringSchema stringSchema =
          new StringSchema(schema, Optional.ofNullable(schema.getFormat()));
      return Optional.of(stringSchema);
    }

    return Optional.empty();
  }

  @Override
  public MapContext mapToPojo(ComponentName name) {
    final PojoMemberReference pojoMemberReference =
        new PojoMemberReference(name.getPojoName(), getDescription(), asType());
    return MapContext.ofPojoMemberReference(pojoMemberReference);
  }

  @Override
  public MemberSchemaMapResult mapToMemberType(ComponentName parentComponentName, Name memberName) {
    return MemberSchemaMapResult.ofType(asType());
  }

  @Override
  public Schema<?> getDelegateSchema() {
    return delegate;
  }

  public Optional<String> getFormat() {
    return format;
  }

  private StringType asType() {
    final Constraints patternConstraints = ConstraintsMapper.getPattern(delegate);
    final Constraints minAndMaxLengthConstraints = ConstraintsMapper.getMinAndMaxLength(delegate);
    final StringType rawStringType = StringType.ofFormat(StringType.Format.NONE);

    final StringType stringType =
        format
            .map(
                formatValue -> {
                  final StringType.Format stringFormat = StringType.Format.parseString(formatValue);
                  return StringType.ofFormatAndValue(stringFormat, formatValue);
                })
            .orElse(rawStringType)
            .withConstraints(patternConstraints.and(minAndMaxLengthConstraints));

    if (stringType.getFormat().equals(StringType.Format.EMAIL)) {
      return stringType.addConstraints(Constraints.ofEmail());
    } else {
      return stringType;
    }
  }
}
