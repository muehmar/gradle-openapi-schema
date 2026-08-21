package com.github.muehmar.gradle.openapi.generator.model.schema;

import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.model.UnresolvedSchemaReference;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.name.SchemaName;
import com.github.muehmar.gradle.openapi.generator.model.specification.SchemaReference;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StandardObjectType;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ReferenceSchema implements OpenApiSchema {
  private final SchemaWrapper delegate;
  private final String reference;

  private ReferenceSchema(SchemaWrapper delegate, String reference) {
    this.delegate = delegate;
    this.reference = reference;
  }

  public static Optional<ReferenceSchema> wrap(SchemaWrapper wrapper) {
    if (wrapper.getSchema().getType() == null
        && wrapper.getSchema().getFormat() == null
        && wrapper.getSchema().get$ref() != null) {
      final ReferenceSchema referenceSchema =
          new ReferenceSchema(wrapper, wrapper.getSchema().get$ref());
      return Optional.of(referenceSchema);
    }

    return Optional.empty();
  }

  @Override
  public MapContext mapToPojo(ComponentName componentName) {
    final SchemaReference schemaReference =
        SchemaReference.fromRefString(delegate.getSpec(), reference);
    final UnresolvedSchemaReference unresolvedSchemaReference =
        new UnresolvedSchemaReference(
            componentName, SchemaName.ofName(schemaReference.getSchemaName()));
    return MapContext.ofUnresolvedSchemaReference(unresolvedSchemaReference)
        .addOpenApiSpec(schemaReference.getRemoteSpec());
  }

  @Override
  public MemberSchemaMapResult mapToMemberType(ComponentName parentComponentName, Name memberName) {
    final SchemaReference schemaReference =
        SchemaReference.fromRefString(delegate.getSpec(), reference);
    final ComponentName name =
        ComponentName.fromSchemaStringAndSuffix(
            schemaReference.getSchemaName().asString(),
            parentComponentName.getPojoName().getSuffix());
    final ObjectType objectType = StandardObjectType.ofName(name.getPojoName());
    return MemberSchemaMapResult.ofType(objectType).addOpenApiSpec(schemaReference.getRemoteSpec());
  }

  public SchemaWrapper getSchemaWrapper() {
    return delegate;
  }

  @Override
  public Schema<?> getDelegateSchema() {
    return delegate.getSchema();
  }

  public String getReference() {
    return reference;
  }
}
