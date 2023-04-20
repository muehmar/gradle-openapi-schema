package com.github.muehmar.gradle.openapi.generator.model.schema;

import com.github.muehmar.gradle.openapi.generator.mapper.ConstraintsMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.pojo.FreeFormPojo;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.model.type.NoType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class FreeFormSchema implements OpenApiSchema {
  private final Schema<?> delegate;

  private FreeFormSchema(Schema<?> delegate) {
    this.delegate = delegate;
  }

  public static Optional<FreeFormSchema> wrap(Schema<?> schema) {
    final Object additionalProperties = schema.getAdditionalProperties();
    if (SchemaType.OBJECT.matchesType(schema)
        && schema.getProperties() == null
        && freeFormAdditionalProperties(additionalProperties)) {

      final FreeFormSchema freeFormSchema = new FreeFormSchema(schema);
      return Optional.of(freeFormSchema);
    }

    return Optional.empty();
  }

  private static boolean freeFormAdditionalProperties(Object additionalProperties) {
    return additionalProperties == null
        || Boolean.TRUE.equals(additionalProperties)
        || isNoSchemaAdditionalProperties(additionalProperties);
  }

  private static boolean isNoSchemaAdditionalProperties(Object additionalProperties) {
    if (additionalProperties instanceof Schema) {
      final Schema<?> schema = (Schema<?>) additionalProperties;
      return schema.getType() == null
          && schema.getTypes() == null
          && schema.getProperties() == null
          && schema.getAdditionalProperties() == null
          && schema.get$ref() == null;
    }
    return false;
  }

  @Override
  public MapContext mapToPojo(PojoName pojoName) {
    final FreeFormPojo freeFormPojo =
        FreeFormPojo.of(pojoName, getDescription(), getFreeFormConstraints());
    return MapContext.ofPojo(freeFormPojo);
  }

  @Override
  public MemberSchemaMapResult mapToMemberType(PojoName pojoName, Name memberName) {
    final MapType mapType = MapType.ofKeyAndValueType(StringType.noFormat(), NoType.create());
    return MemberSchemaMapResult.ofType(mapType);
  }

  @Override
  public Schema<?> getDelegateSchema() {
    return delegate;
  }

  private Constraints getFreeFormConstraints() {
    return ConstraintsMapper.getPropertyCountConstraints(delegate);
  }
}
