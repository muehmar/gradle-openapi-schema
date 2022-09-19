package com.github.muehmar.gradle.openapi.generator.mapper;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import io.github.muehmar.pojoextension.annotations.SafeBuilder;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@SafeBuilder
@EqualsAndHashCode
@ToString
public class UnmappedItems {
  private final PList<OpenApiSpec> specifications;
  private final PList<PojoSchema> pojoSchemas;

  UnmappedItems(PList<OpenApiSpec> specifications, PList<PojoSchema> pojoSchemas) {
    this.specifications = specifications;
    this.pojoSchemas = pojoSchemas;
  }

  public static UnmappedItems empty() {
    return new UnmappedItems(PList.empty(), PList.empty());
  }

  public static UnmappedItems ofSpec(OpenApiSpec spec) {
    return new UnmappedItems(PList.single(spec), PList.empty());
  }

  public static UnmappedItems ofPojoSchema(PojoSchema pojoSchema) {
    return new UnmappedItems(PList.empty(), PList.single(pojoSchema));
  }

  public PList<OpenApiSpec> getSpecifications() {
    return specifications;
  }

  public PList<PojoSchema> getPojoSchemas() {
    return pojoSchemas;
  }

  public UnmappedItems merge(UnmappedItems other) {
    return new UnmappedItems(
        specifications.concat(other.specifications), pojoSchemas.concat(other.pojoSchemas));
  }

  public UnmappedItems addPojoSchemas(PList<PojoSchema> pojoSchemas) {
    return new UnmappedItems(specifications, this.pojoSchemas.concat(pojoSchemas));
  }

  public UnmappedItems addSpecification(Optional<OpenApiSpec> spec) {
    return new UnmappedItems(specifications.concat(PList.fromOptional(spec)), pojoSchemas);
  }

  public <T> T onUnmappedItems(
      BiFunction<UnmappedItems, NonEmptyList<OpenApiSpec>, T> onSpecifications,
      BiFunction<UnmappedItems, NonEmptyList<PojoSchema>, T> onSchemas,
      Supplier<T> onNoItemUnmapped) {
    return NonEmptyList.fromIter(specifications)
        .map(
            specs -> {
              final UnmappedItems nextUnmappedItems = new UnmappedItems(PList.empty(), pojoSchemas);
              return onSpecifications.apply(nextUnmappedItems, specs);
            })
        .orElseGet(() -> onUnmappedPojoSchema(onSchemas, onNoItemUnmapped));
  }

  private <T> T onUnmappedPojoSchema(
      BiFunction<UnmappedItems, NonEmptyList<PojoSchema>, T> onNextSchema,
      Supplier<T> onNoItemUnmapped) {
    return NonEmptyList.fromIter(pojoSchemas)
        .map(
            schemas -> {
              final UnmappedItems nextUnmappedItems =
                  new UnmappedItems(specifications, PList.empty());
              return onNextSchema.apply(nextUnmappedItems, schemas);
            })
        .orElseGet(onNoItemUnmapped);
  }
}
