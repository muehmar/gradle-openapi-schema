package com.github.muehmar.gradle.openapi.generator.model.schema;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.pojo.EnumPojo;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
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
    final Type enumType =
        EnumType.ofNameAndMembersAndFormat(
            memberName.startUpperCase().append("Enum"), enums, format);

    return MemberSchemaMapResult.ofType(enumType);
  }

  @Override
  public Schema<?> getDelegateSchema() {
    return delegate;
  }
}
