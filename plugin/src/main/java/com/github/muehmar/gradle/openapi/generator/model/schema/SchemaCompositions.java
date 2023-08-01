package com.github.muehmar.gradle.openapi.generator.model.schema;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.model.Discriminator;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.specification.SchemaReference;
import io.swagger.v3.oas.models.media.Schema;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Value;

/** Wrapper for a schema to extract the compositions. */
class SchemaCompositions {
  private final Schema<?> delegate;

  public SchemaCompositions(Schema<?> delegate) {
    this.delegate = delegate;
  }

  public static SchemaCompositions wrap(Schema<?> delegate) {
    return new SchemaCompositions(delegate);
  }

  public CompositionMapResult<UnresolvedAllOfComposition> getAllOf(PojoName pojoName) {
    return mapSchemasToPojoNames(pojoName, Schema::getAllOf, ComposedSchemas.CompositionType.ALL_OF)
        .map(
            result ->
                new CompositionMapResult<>(
                    result.getUnmappedItems(),
                    UnresolvedAllOfComposition.fromPojoNames(result.getPojoNames())))
        .orElseGet(CompositionMapResult::empty);
  }

  public CompositionMapResult<UnresolvedOneOfComposition> getOneOf(PojoName pojoName) {
    return mapSchemasToPojoNames(pojoName, Schema::getOneOf, ComposedSchemas.CompositionType.ONE_OF)
        .map(
            result ->
                new CompositionMapResult<>(
                    result.getUnmappedItems(),
                    UnresolvedOneOfComposition.fromPojoNamesAndDiscriminator(
                        result.getPojoNames(), extractDiscriminator())))
        .orElseGet(CompositionMapResult::empty);
  }

  public CompositionMapResult<UnresolvedAnyOfComposition> getAnyOf(PojoName pojoName) {
    return mapSchemasToPojoNames(pojoName, Schema::getAnyOf, ComposedSchemas.CompositionType.ANY_OF)
        .map(
            result ->
                new CompositionMapResult<>(
                    result.getUnmappedItems(),
                    UnresolvedAnyOfComposition.fromPojoNames(result.getPojoNames())))
        .orElseGet(CompositionMapResult::empty);
  }

  private Optional<ComposedSchemas.ComposedSchemasMapResult> mapSchemasToPojoNames(
      PojoName pojoName,
      Function<Schema<?>, List<Schema>> getCompositions,
      ComposedSchemas.CompositionType type) {
    return Optional.ofNullable(getCompositions.apply(delegate))
        .map(PList::fromIter)
        .map(schemas -> schemas.map(OpenApiSchema::wrapSchema))
        .map(ComposedSchemas::fromSchemas)
        .map(s -> s.mapSchemasToPojoNames(pojoName, type));
  }

  private Optional<Discriminator> extractDiscriminator() {
    return Optional.ofNullable(delegate.getDiscriminator())
        .filter(discriminator -> discriminator.getPropertyName() != null)
        .map(this::fromOpenApiDiscriminator);
  }

  private Discriminator fromOpenApiDiscriminator(
      io.swagger.v3.oas.models.media.Discriminator oasDiscriminator) {
    final Name propertyName = Name.ofString(oasDiscriminator.getPropertyName());
    final Optional<Map<String, Name>> pojoNameMapping =
        Optional.ofNullable(oasDiscriminator.getMapping())
            .map(this::fromOpenApiDiscriminatorMapping);
    return Discriminator.fromPropertyName(propertyName).withMapping(pojoNameMapping);
  }

  private Map<String, Name> fromOpenApiDiscriminatorMapping(Map<String, String> mapping) {
    return mapping.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, e -> mapMappingReference(e.getValue())));
  }

  private Name mapMappingReference(String reference) {
    return SchemaReference.fromRefString(reference).getSchemaName();
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
