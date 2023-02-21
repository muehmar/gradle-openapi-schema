package com.github.muehmar.gradle.openapi.generator.mapper;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.ParameterSchema;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** Collection of items which are not yet mapped into an internal model. */
@PojoBuilder
@EqualsAndHashCode
@ToString
public class UnmappedItems {
  private final PList<OpenApiSpec> specifications;
  private final PList<PojoSchema> pojoSchemas;
  private final PList<ParameterSchema> parameterSchemas;

  UnmappedItems(
      PList<OpenApiSpec> specifications,
      PList<PojoSchema> pojoSchemas,
      PList<ParameterSchema> parameterSchemas) {
    this.specifications = specifications;
    this.pojoSchemas = pojoSchemas;
    this.parameterSchemas = parameterSchemas;
  }

  public static UnmappedItems empty() {
    return new UnmappedItems(PList.empty(), PList.empty(), PList.empty());
  }

  public static UnmappedItems ofSpec(OpenApiSpec spec) {
    return new UnmappedItems(PList.single(spec), PList.empty(), PList.empty());
  }

  public static UnmappedItems ofPojoSchema(PojoSchema pojoSchema) {
    return new UnmappedItems(PList.empty(), PList.single(pojoSchema), PList.empty());
  }

  public static UnmappedItems ofPojoSchemas(PList<PojoSchema> pojoSchemas) {
    return new UnmappedItems(PList.empty(), pojoSchemas, PList.empty());
  }

  public static UnmappedItems ofParameterSchema(ParameterSchema parameterSchema) {
    return new UnmappedItems(PList.empty(), PList.empty(), PList.single(parameterSchema));
  }

  public boolean isEmpty() {
    return specifications.isEmpty() && pojoSchemas.isEmpty() && parameterSchemas.isEmpty();
  }

  public boolean nonEmpty() {
    return not(isEmpty());
  }

  public PList<OpenApiSpec> getSpecifications() {
    return specifications;
  }

  public PList<PojoSchema> getPojoSchemas() {
    return pojoSchemas;
  }

  public UnmappedItems merge(UnmappedItems other) {
    return new UnmappedItems(
        specifications.concat(other.specifications),
        pojoSchemas.concat(other.pojoSchemas),
        parameterSchemas);
  }

  public UnmappedItems addPojoSchemas(PList<PojoSchema> otherSchemas) {
    return new UnmappedItems(specifications, pojoSchemas.concat(otherSchemas), parameterSchemas);
  }

  public UnmappedItems addParameterSchemas(PList<ParameterSchema> otherSchemas) {
    return new UnmappedItems(specifications, pojoSchemas, parameterSchemas.concat(otherSchemas));
  }

  public UnmappedItems addSpecification(Optional<OpenApiSpec> spec) {
    return new UnmappedItems(
        specifications.concat(PList.fromOptional(spec)), pojoSchemas, parameterSchemas);
  }

  public <T> T onUnmappedItems(
      BiFunction<UnmappedItems, NonEmptyList<OpenApiSpec>, T> onSpecifications,
      BiFunction<UnmappedItems, NonEmptyList<PojoSchema>, T> onSchemas,
      BiFunction<UnmappedItems, NonEmptyList<ParameterSchema>, T> onParameters,
      Supplier<T> onNoItemUnmapped) {
    return NonEmptyList.fromIter(specifications)
        .map(
            specs -> {
              final UnmappedItems nextUnmappedItems =
                  new UnmappedItems(PList.empty(), pojoSchemas, parameterSchemas);
              return onSpecifications.apply(nextUnmappedItems, specs);
            })
        .orElseGet(() -> onUnmappedPojoSchema(onSchemas, onParameters, onNoItemUnmapped));
  }

  private <T> T onUnmappedPojoSchema(
      BiFunction<UnmappedItems, NonEmptyList<PojoSchema>, T> onSchemas,
      BiFunction<UnmappedItems, NonEmptyList<ParameterSchema>, T> onParameters,
      Supplier<T> onNoItemUnmapped) {
    return NonEmptyList.fromIter(pojoSchemas)
        .map(
            schemas -> {
              final UnmappedItems nextUnmappedItems =
                  new UnmappedItems(specifications, PList.empty(), parameterSchemas);
              return onSchemas.apply(nextUnmappedItems, schemas);
            })
        .orElseGet(() -> onUnmappedParameter(onParameters, onNoItemUnmapped));
  }

  private <T> T onUnmappedParameter(
      BiFunction<UnmappedItems, NonEmptyList<ParameterSchema>, T> onParameters,
      Supplier<T> onNoItemUnmapped) {
    return NonEmptyList.fromIter(parameterSchemas)
        .map(
            parameter -> {
              final UnmappedItems nextUnmappedItems =
                  new UnmappedItems(specifications, pojoSchemas, PList.empty());
              return onParameters.apply(nextUnmappedItems, parameter);
            })
        .orElseGet(onNoItemUnmapped);
  }
}
