package com.github.muehmar.gradle.openapi.generator.model.schema;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.pojo.EnumPojo;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumTypeBuilder;
import io.swagger.v3.oas.models.SpecVersion;
import io.swagger.v3.oas.models.media.Schema;
import java.util.List;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class EnumSchema implements OpenApiSchema {
  private final Schema<?> delegate;
  private final PList<String> enums;

  private EnumSchema(Schema<?> delegate, PList<String> enums) {
    this.delegate = delegate;
    this.enums = enums;
  }

  public static Optional<EnumSchema> wrap(SchemaWrapper wrapper) {
    final Schema<?> schema = wrapper.getSchema();
    final List<?> enums = schema.getEnum();
    if (SchemaType.STRING.matchesType(schema) && enums != null) {
      final EnumSchema enumSchema =
          new EnumSchema(schema, PList.fromIter(enums).map(String.class::cast));
      return Optional.of(enumSchema);
    }

    return Optional.empty();
  }

  @Override
  public MapContext mapToPojo(ComponentName name) {
    final EnumPojo enumPojo = EnumPojo.of(name, getDescription(), enums);
    return MapContext.ofPojo(enumPojo);
  }

  @Override
  public MemberSchemaMapResult mapToMemberType(ComponentName parentComponentName, Name memberName) {
    final Optional<String> format = Optional.ofNullable(delegate.getFormat());
    final Nullability legacyNullability =
        delegate.getSpecVersion().equals(SpecVersion.V30)
            ? Optional.ofNullable(delegate.getNullable())
                .map(Nullability::fromBoolean)
                .orElse(Nullability.NOT_NULLABLE)
            : Nullability.NOT_NULLABLE;

    final Nullability nullability =
        delegate.getSpecVersion().equals(SpecVersion.V31)
            ? Optional.ofNullable(delegate.getTypes())
                .map(types -> types.contains(SchemaType.NULL.asString()))
                .map(Nullability::fromBoolean)
                .orElse(Nullability.NOT_NULLABLE)
            : Nullability.NOT_NULLABLE;

    final Type enumType =
        EnumTypeBuilder.createFull()
            .name(memberName.startUpperCase().append("Enum"))
            .members(enums)
            .nullability(nullability)
            .legacyNullability(legacyNullability)
            .format(format)
            .build();

    return MemberSchemaMapResult.ofType(enumType);
  }

  @Override
  public Schema<?> getDelegateSchema() {
    return delegate;
  }
}
