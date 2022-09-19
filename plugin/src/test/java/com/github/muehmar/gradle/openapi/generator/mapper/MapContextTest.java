package com.github.muehmar.gradle.openapi.generator.mapper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.pojo.EnumPojo;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.function.BiFunction;
import org.junit.jupiter.api.Test;

class MapContextTest {
  @Test
  void onUnmappedItems_when_noUnmappedItems_then_resultReturned() {
    final UnmappedItems unmappedItems = UnmappedItems.empty();
    final MapContext mapContext =
        MapContext.fromUnmappedItemsAndResult(unmappedItems, MapResult.empty());

    final BiFunction<MapContext, NonEmptyList<OpenApiSpec>, MapResult> onSpecifications =
        (ctx, openApiSpecs) -> {
          throw new IllegalStateException("onSpecifications called");
        };
    final BiFunction<MapContext, NonEmptyList<PojoSchema>, MapResult> onSchemas =
        (ctx, schemas) -> {
          throw new IllegalStateException("onSchemas called");
        };

    // method call
    final MapResult mapResult =
        assertDoesNotThrow(() -> mapContext.onUnmappedItems(onSpecifications, onSchemas));

    assertEquals(MapResult.empty(), mapResult);
  }

  @Test
  void onUnmappedItems_when_openApiSpec_then_onSpecificationsCalled() {
    final MapResult expectedMapResult =
        MapResult.ofPojo(
            EnumPojo.of(
                PojoName.ofNameAndSuffix("Enum", "Dto"), "Desc", PList.of("member1", "member2")));
    final OpenApiSpec spec = OpenApiSpec.fromString("../components-yml");
    final UnmappedItems unmappedItems = UnmappedItems.ofSpec(spec);
    final MapContext mapContext =
        MapContext.fromUnmappedItemsAndResult(unmappedItems, MapResult.empty());

    final BiFunction<MapContext, NonEmptyList<OpenApiSpec>, MapResult> onSpecifications =
        (ctx, openApiSpecs) -> {
          assertEquals(NonEmptyList.single(spec), openApiSpecs);
          assertEquals(UnmappedItems.empty(), ctx.getUnmappedItems());
          return expectedMapResult;
        };
    final BiFunction<MapContext, NonEmptyList<PojoSchema>, MapResult> onSchemas =
        (ctx, schemas) -> {
          throw new IllegalStateException("onSchemas called");
        };

    // method call
    final MapResult mapResult =
        assertDoesNotThrow(() -> mapContext.onUnmappedItems(onSpecifications, onSchemas));

    assertEquals(expectedMapResult, mapResult);
  }

  @Test
  void onUnmappedItems_when_pojoSchema_then_onSchemasCalled() {
    final MapResult expectedMapResult =
        MapResult.ofPojo(
            EnumPojo.of(
                PojoName.ofNameAndSuffix("Enum", "Dto"), "Desc", PList.of("member1", "member2")));
    final PojoSchema pojoSchema =
        new PojoSchema(PojoName.ofNameAndSuffix("Schema", "Dto"), new StringSchema());
    final UnmappedItems unmappedItems = UnmappedItems.ofPojoSchema(pojoSchema);
    final MapContext mapContext =
        MapContext.fromUnmappedItemsAndResult(unmappedItems, MapResult.empty());

    final BiFunction<MapContext, NonEmptyList<OpenApiSpec>, MapResult> onSpecifications =
        (ctx, openApiSpecs) -> {
          throw new IllegalStateException("onSpecifications called");
        };
    final BiFunction<MapContext, NonEmptyList<PojoSchema>, MapResult> onSchemas =
        (ctx, schemas) -> {
          assertEquals(NonEmptyList.single(pojoSchema), schemas);
          assertEquals(UnmappedItems.empty(), ctx.getUnmappedItems());
          return expectedMapResult;
        };

    // method call
    final MapResult mapResult =
        assertDoesNotThrow(() -> mapContext.onUnmappedItems(onSpecifications, onSchemas));

    assertEquals(expectedMapResult, mapResult);
  }
}
