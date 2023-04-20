package com.github.muehmar.gradle.openapi.generator.model.schema;

import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoMemberReference;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.BooleanType;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class BooleanSchema implements OpenApiSchema {
  private final Schema<?> delegate;

  private BooleanSchema(Schema<?> delegate) {
    this.delegate = delegate;
  }

  public static Optional<BooleanSchema> wrap(Schema<?> schema) {
    if (SchemaType.BOOLEAN.matchesType(schema)) {
      final BooleanSchema booleanSchema = new BooleanSchema(schema);
      return Optional.of(booleanSchema);
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

  private BooleanType asType() {
    return BooleanType.create();
  }
}
