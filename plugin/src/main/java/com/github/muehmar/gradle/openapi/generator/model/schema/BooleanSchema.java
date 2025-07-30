package com.github.muehmar.gradle.openapi.generator.model.schema;

import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.PojoMemberReference;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
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

  public static Optional<BooleanSchema> wrap(SchemaWrapper wrapper) {
    if (SchemaType.BOOLEAN.matchesType(wrapper.getSchema())) {
      final BooleanSchema booleanSchema = new BooleanSchema(wrapper.getSchema());
      return Optional.of(booleanSchema);
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

  private BooleanType asType() {
    return BooleanType.create(Nullability.fromBoolean(isNullable()));
  }
}
