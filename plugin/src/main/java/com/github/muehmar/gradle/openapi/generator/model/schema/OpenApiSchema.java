package com.github.muehmar.gradle.openapi.generator.model.schema;

import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.util.Optionals;
import io.swagger.v3.oas.models.SpecVersion;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;
import java.util.function.Function;

public interface OpenApiSchema {

  static OpenApiSchema wrapSchema(Schema<?> schema) {
    return NonEmptyList.<Function<Schema<?>, Optional<? extends OpenApiSchema>>>single(
            ArraySchema::wrap)
        .add(BooleanSchema::wrap)
        .add(IntegerSchema::wrap)
        .add(NumberSchema::wrap)
        .add(ObjectSchema::wrap)
        .add(ReferenceSchema::wrap)
        .add(StringSchema::wrap)
        .add(EnumSchema::wrap)
        .add(NoTypeSchema::wrap)
        .map(f -> f.apply(schema))
        .reduce(Optionals::or)
        .<OpenApiSchema>map(s -> s)
        .orElse(UnknownSchema.wrap(schema));
  }

  /** Maps the current schema to a {@link Pojo} returned within a {@link MapContext}. */
  MapContext mapToPojo(ComponentName name);

  /**
   * Maps the current schema to a member-type {@link Type} of a pojo returned within a {@link
   * MemberSchemaMapResult}.
   */
  MemberSchemaMapResult mapToMemberType(ComponentName parentComponentName, Name memberName);

  Schema<?> getDelegateSchema();

  default String getDescription() {
    return Optional.ofNullable(getDelegateSchema().getDescription()).orElse("");
  }

  default boolean isNullable() {
    final Schema<?> delegateSchema = getDelegateSchema();
    if (delegateSchema.getSpecVersion().equals(SpecVersion.V30)) {
      return Optional.ofNullable(delegateSchema.getNullable()).orElse(false);
    } else {
      return Optional.ofNullable(delegateSchema.getTypes())
          .map(types -> types.contains(SchemaType.NULL.asString()))
          .orElse(false);
    }
  }
}
