package com.github.muehmar.gradle.openapi.generator.model.schema;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import io.swagger.v3.oas.models.media.Schema;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.Value;

/** Wrapper for a schema to extract the compositions. */
class SchemaCompositions {
  private final SchemaWrapper delegate;

  public SchemaCompositions(SchemaWrapper delegate) {
    this.delegate = delegate;
  }

  public static SchemaCompositions wrap(SchemaWrapper delegate) {
    return new SchemaCompositions(delegate);
  }

  public CompositionMapResult<UnresolvedAllOfComposition> getAllOf(ComponentName name) {
    return mapSchemasToPojoNames(name, Schema::getAllOf, ComposedSchemas.CompositionType.ALL_OF)
        .map(
            result ->
                new CompositionMapResult<>(
                    result.getUnmappedItems(),
                    UnresolvedAllOfComposition.fromComponentNames(result.getComponentNames())))
        .orElseGet(CompositionMapResult::empty);
  }

  public CompositionMapResult<UnresolvedOneOfComposition> getOneOf(ComponentName name) {
    return mapSchemasToPojoNames(name, Schema::getOneOf, ComposedSchemas.CompositionType.ONE_OF)
        .map(
            result ->
                new CompositionMapResult<>(
                    result.getUnmappedItems(),
                    UnresolvedOneOfComposition.fromComponentNames(result.getComponentNames())))
        .orElseGet(CompositionMapResult::empty);
  }

  public CompositionMapResult<UnresolvedAnyOfComposition> getAnyOf(ComponentName name) {
    return mapSchemasToPojoNames(name, Schema::getAnyOf, ComposedSchemas.CompositionType.ANY_OF)
        .map(
            result ->
                new CompositionMapResult<>(
                    result.getUnmappedItems(),
                    UnresolvedAnyOfComposition.fromPojoNames(result.getComponentNames())))
        .orElseGet(CompositionMapResult::empty);
  }

  private Optional<ComposedSchemas.ComposedSchemasMapResult> mapSchemasToPojoNames(
      ComponentName name,
      Function<Schema<?>, List<Schema>> getCompositions,
      ComposedSchemas.CompositionType type) {
    return Optional.ofNullable(getCompositions.apply(delegate.getSchema()))
        .map(PList::fromIter)
        .map(
            schemas ->
                schemas.map(
                    s -> OpenApiSchema.wrapSchema(new SchemaWrapper(delegate.getSpec(), s))))
        .map(ComposedSchemas::fromSchemas)
        .map(s -> s.mapSchemasToPojoNames(name, type));
  }

  @Value
  public static class CompositionMapResult<T> {
    UnmappedItems unmappedItems;
    Optional<T> composition;

    public CompositionMapResult(UnmappedItems unmappedItems, Optional<T> composition) {
      this.unmappedItems = unmappedItems;
      this.composition = composition;
    }

    public CompositionMapResult(UnmappedItems unmappedItems, T composition) {
      this(unmappedItems, Optional.of(composition));
    }

    public static <T> CompositionMapResult<T> empty() {
      return new CompositionMapResult<>(UnmappedItems.empty(), Optional.empty());
    }
  }
}
