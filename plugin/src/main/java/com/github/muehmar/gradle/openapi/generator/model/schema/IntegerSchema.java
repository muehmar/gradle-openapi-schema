package com.github.muehmar.gradle.openapi.generator.model.schema;

import com.github.muehmar.gradle.openapi.generator.mapper.ConstraintsMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.PojoMemberReference;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class IntegerSchema implements OpenApiSchema {
  private final Schema<?> delegate;
  private final Optional<String> format;

  private IntegerSchema(Schema<?> delegate, Optional<String> format) {
    this.delegate = delegate;
    this.format = format;
  }

  public static Optional<IntegerSchema> wrap(Schema<?> schema) {
    if (SchemaType.INTEGER.matchesType(schema)) {
      final IntegerSchema integerSchema =
          new IntegerSchema(schema, Optional.ofNullable(schema.getFormat()));
      return Optional.of(integerSchema);
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
    final IntegerType integerType = asType();
    return MemberSchemaMapResult.ofType(integerType);
  }

  @Override
  public Schema<?> getDelegateSchema() {
    return delegate;
  }

  public Optional<String> getFormat() {
    return format;
  }

  private IntegerType asType() {
    final Constraints constraints =
        ConstraintsMapper.getMinimumAndMaximum(delegate)
            .and(ConstraintsMapper.getMultipleOf(delegate));
    final IntegerType.Format integerFormat =
        format.flatMap(IntegerType.Format::parseString).orElse(IntegerType.Format.INTEGER);

    return IntegerType.ofFormat(integerFormat, Nullability.fromNullableBoolean(isNullable()))
        .withConstraints(constraints);
  }
}
