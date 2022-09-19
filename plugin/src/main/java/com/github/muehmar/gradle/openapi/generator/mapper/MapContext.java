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

  private final MapResult mapResult;

  private MapContext(UnmappedItems unmappedItems, MapResult mapResult) {
    this.unmappedItems = unmappedItems;
    this.mapResult = mapResult;
  }

  public static MapContext empty() {
    return new MapContext(UnmappedItems.empty(), MapResult.empty());
  }

  public static MapContext fromInitialSpecification(OpenApiSpec initialSpec) {
    return new MapContext(UnmappedItems.ofSpec(initialSpec), MapResult.empty());
  }

  public static MapContext fromUnmappedItemsAndResult(
      UnmappedItems unmappedItems, MapResult mapResult) {
    return new MapContext(unmappedItems, mapResult);
  }

  public static MapContext ofPojo(Pojo pojo) {
    return new MapContext(UnmappedItems.empty(), MapResult.ofPojo(pojo));
  }

  public MapResult onUnmappedItems(
      BiFunction<MapContext, NonEmptyList<OpenApiSpec>, MapResult> onSpecifications,
      BiFunction<MapContext, NonEmptyList<PojoSchema>, MapResult> onPojoSchemas) {
    return unmappedItems.onUnmappedItems(
        (newUnmappedItems, specs) -> {
          final MapContext mapContext = new MapContext(newUnmappedItems, mapResult);
          return onSpecifications.apply(mapContext, specs);
        },
        (newUnmappedItems, pojoSchemas) -> {
          final MapContext mapContext = new MapContext(newUnmappedItems, mapResult);
          return onPojoSchemas.apply(mapContext, pojoSchemas);
        },
        () -> mapResult);
  }

  public MapContext merge(MapContext other) {
    return new MapContext(
        unmappedItems.merge(other.unmappedItems), mapResult.merge(other.mapResult));
  }

  public MapContext addPojoSchemas(PList<PojoSchema> pojoSchemas) {
    return new MapContext(unmappedItems.addPojoSchemas(pojoSchemas), mapResult);
  }

  public UnmappedItems getUnmappedItems() {
    return unmappedItems;
  }

  public MapResult getMapResult() {
    return mapResult;
  }
}
