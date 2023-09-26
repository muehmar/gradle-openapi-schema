package com.github.muehmar.gradle.openapi.generator.model.schema;

import com.github.muehmar.gradle.openapi.exception.OpenApiGeneratorException;
import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.specification.SchemaReference;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ReferenceSchema implements OpenApiSchema {
  private final Schema<?> delegate;
  private final String reference;

  private ReferenceSchema(Schema<?> delegate, String reference) {
    this.delegate = delegate;
    this.reference = reference;
  }

  public static Optional<ReferenceSchema> wrap(Schema<?> schema) {
    if (schema.getType() == null && schema.getFormat() == null && schema.get$ref() != null) {
      final ReferenceSchema referenceSchema = new ReferenceSchema(schema, schema.get$ref());
      return Optional.of(referenceSchema);
    }

    return Optional.empty();
  }

  @Override
  public MapContext mapToPojo(ComponentName componentName) {
    throw new OpenApiGeneratorException(
        "A reference schema is currently not supported as root schema.");
  }

  @Override
  public MemberSchemaMapResult mapToMemberType(ComponentName parentComponentName, Name memberName) {
    final SchemaReference schemaReference = SchemaReference.fromRefString(reference);
    final ComponentName name =
        ComponentName.fromSchemaStringAndSuffix(
            schemaReference.getSchemaName().asString(),
            parentComponentName.getPojoName().getSuffix());
    final ObjectType objectType = ObjectType.ofName(name.getPojoName());
    return MemberSchemaMapResult.ofType(objectType).addOpenApiSpec(schemaReference.getRemoteSpec());
  }

  @Override
  public Schema<?> getDelegateSchema() {
    return delegate;
  }

  public String getReference() {
    return reference;
  }
}
