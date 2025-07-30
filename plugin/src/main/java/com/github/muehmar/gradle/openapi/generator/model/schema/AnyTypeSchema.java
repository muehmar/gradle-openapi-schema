package com.github.muehmar.gradle.openapi.generator.model.schema;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;

import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.model.PojoMemberReference;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.type.AnyType;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class AnyTypeSchema implements OpenApiSchema {
  private final Schema<?> delegate;

  private AnyTypeSchema(Schema<?> delegate) {
    this.delegate = delegate;
  }

  public static Optional<AnyTypeSchema> wrap(SchemaWrapper wrapper) {
    final Schema<?> schema = wrapper.getSchema();
    if (schema.getType() == null
        && schema.getTypes() == null
        && schema.getFormat() == null
        && schema.getProperties() == null
        && schema.getRequired() == null
        && schema.getAdditionalProperties() == null
        && schema.get$ref() == null) {

      final AnyTypeSchema anyTypeSchema = new AnyTypeSchema(schema);
      return Optional.of(anyTypeSchema);
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

  private AnyType asType() {
    return AnyType.create(NULLABLE);
  }
}
