package com.github.muehmar.gradle.openapi.generator.model.schema;

import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoMemberReference;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.NoType;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class NoTypeSchema implements OpenApiSchema {
  private final Schema<?> delegate;

  private NoTypeSchema(Schema<?> delegate) {
    this.delegate = delegate;
  }

  public static Optional<NoTypeSchema> wrap(Schema<?> schema) {
    if (schema.getType() == null
        && schema.getTypes() == null
        && schema.getFormat() == null
        && schema.getProperties() == null
        && schema.getAdditionalProperties() == null
        && schema.get$ref() == null) {

      final NoTypeSchema noTypeSchema = new NoTypeSchema(schema);
      return Optional.of(noTypeSchema);
    }

    return Optional.empty();
  }

  @Override
  public MapContext mapToPojo(PojoName pojoName) {
    final PojoMemberReference pojoMemberReference =
        new PojoMemberReference(pojoName, getDescription(), asType());
    return MapContext.ofPojoMemberReference(pojoMemberReference);
  }

  @Override
  public MemberSchemaMapResult mapToMemberType(PojoName pojoName, Name memberName) {
    return MemberSchemaMapResult.ofType(asType());
  }

  @Override
  public Schema<?> getDelegateSchema() {
    return delegate;
  }

  private NoType asType() {
    return NoType.create();
  }
}