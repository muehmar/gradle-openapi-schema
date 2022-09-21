package com.github.muehmar.gradle.openapi.generator.mapper;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import java.util.function.BiFunction;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class MapContext {
  private final UnmappedItems unmappedItems;

  private final UnresolvedMapResult unresolvedMapResult;

  private MapContext(UnmappedItems unmappedItems, UnresolvedMapResult unresolvedMapResult) {
    this.unmappedItems = unmappedItems;
    this.unresolvedMapResult = unresolvedMapResult;
  }

  public static MapContext empty() {
    return new MapContext(UnmappedItems.empty(), UnresolvedMapResult.empty());
  }

  public static MapContext fromInitialSpecification(OpenApiSpec initialSpec) {
    return new MapContext(UnmappedItems.ofSpec(initialSpec), UnresolvedMapResult.empty());
  }

  public static MapContext fromUnmappedItemsAndResult(
      UnmappedItems unmappedItems, UnresolvedMapResult unresolvedMapResult) {
    return new MapContext(unmappedItems, unresolvedMapResult);
  }

  public static MapContext ofPojo(Pojo pojo) {
    return new MapContext(UnmappedItems.empty(), UnresolvedMapResult.ofPojo(pojo));
  }

  public UnresolvedMapResult onUnmappedItems(
      BiFunction<MapContext, NonEmptyList<OpenApiSpec>, UnresolvedMapResult> onSpecifications,
      BiFunction<MapContext, NonEmptyList<PojoSchema>, UnresolvedMapResult> onPojoSchemas) {
    return unmappedItems.onUnmappedItems(
        (newUnmappedItems, specs) -> {
          final MapContext mapContext = new MapContext(newUnmappedItems, unresolvedMapResult);
          final UnresolvedMapResult usedSpecsResult =
              UnresolvedMapResult.ofUsedSpecs(specs.toPList());
          return onSpecifications.apply(mapContext, specs).merge(usedSpecsResult);
        },
        (newUnmappedItems, pojoSchemas) -> {
          final MapContext mapContext = new MapContext(newUnmappedItems, unresolvedMapResult);
          return onPojoSchemas.apply(mapContext, pojoSchemas);
        },
        () -> unresolvedMapResult);
  }

  public MapContext merge(MapContext other) {
    return new MapContext(
        unmappedItems.merge(other.unmappedItems),
        unresolvedMapResult.merge(other.unresolvedMapResult));
  }

  public MapContext addPojoSchemas(PList<PojoSchema> pojoSchemas) {
    return new MapContext(unmappedItems.addPojoSchemas(pojoSchemas), unresolvedMapResult);
  }

  public UnmappedItems getUnmappedItems() {
    return unmappedItems;
  }

  public UnresolvedMapResult getUnresolvedMapResult() {
    return unresolvedMapResult;
  }
}