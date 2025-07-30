package com.github.muehmar.gradle.openapi.generator.model.schema;

import com.github.muehmar.gradle.openapi.generator.mapper.ConstraintsMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.PojoMemberReference;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class NumberSchema implements OpenApiSchema {
  private final Schema<?> delegate;
  private final Optional<String> format;

  private NumberSchema(Schema<?> delegate, Optional<String> format) {
    this.delegate = delegate;
    this.format = format;
  }

  public static Optional<NumberSchema> wrap(SchemaWrapper wrapper) {
    final Schema<?> schema = wrapper.getSchema();
    if (SchemaType.NUMBER.matchesType(schema)) {
      final NumberSchema numberSchema =
          new NumberSchema(schema, Optional.ofNullable(schema.getFormat()));
      return Optional.of(numberSchema);
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
    final NumericType numericType = asType();
    return MemberSchemaMapResult.ofType(numericType);
  }

  @Override
  public Schema<?> getDelegateSchema() {
    return delegate;
  }

  public Optional<String> getFormat() {
    return format;
  }

  private NumericType asType() {
    final Constraints constraints =
        ConstraintsMapper.getDecimalMinimumAndMaximum(delegate)
            .and(ConstraintsMapper.getMultipleOf(delegate));
    final NumericType.Format numberFormat =
        format.flatMap(NumericType.Format::parseString).orElse(NumericType.Format.FLOAT);

    return NumericType.ofFormat(numberFormat, Nullability.fromBoolean(isNullable()))
        .withConstraints(constraints);
  }
}
