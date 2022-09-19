package com.github.muehmar.gradle.openapi.generator.mapper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

class UnmappedItemsTest {
  @Test
  void onUnmappedItems_when_empty_then_resultSupplierCalled() {
    final UnmappedItems empty = UnmappedItems.empty();

    final BiFunction<UnmappedItems, NonEmptyList<OpenApiSpec>, MapResult> onSpecifications =
        (unmappedItems, openApiSpecs) -> {
          throw new IllegalStateException("onSpecifications called");
        };
    final BiFunction<UnmappedItems, NonEmptyList<PojoSchema>, MapResult> onSchemas =
        (unmappedItems, schemas) -> {
          throw new IllegalStateException("onSchemas called");
        };
    final Supplier<MapResult> onNoItemUnmapped = MapResult::empty;

    // method call
    final MapResult mapResult =
        assertDoesNotThrow(
            () -> empty.onUnmappedItems(onSpecifications, onSchemas, onNoItemUnmapped));

    assertEquals(MapResult.empty(), mapResult);
  }

  @Test
  void onUnmappedItems_when_openApiSpec_then_onSpecificationsCalled() {
    final OpenApiSpec spec = OpenApiSpec.fromString("../components-yml");
    final UnmappedItems unmappedItems = UnmappedItems.ofSpec(spec);

    final BiFunction<UnmappedItems, NonEmptyList<OpenApiSpec>, MapResult> onSpecifications =
        (newUnmappedItems, openApiSpecs) -> {
          assertEquals(NonEmptyList.single(spec), openApiSpecs);
          assertEquals(PList.empty(), newUnmappedItems.getSpecifications());
          return MapResult.empty();
        };
    final BiFunction<UnmappedItems, NonEmptyList<PojoSchema>, MapResult> onSchemas =
        (newUnmappedItems, schemas) -> {
          throw new IllegalStateException("onSchemas called");
        };
    final Supplier<MapResult> onNoItemUnmapped =
        () -> {
          throw new IllegalStateException("onNoItemUnmapped called");
        };

    // method call
    final MapResult mapResult =
        assertDoesNotThrow(
            () -> unmappedItems.onUnmappedItems(onSpecifications, onSchemas, onNoItemUnmapped));

    assertEquals(MapResult.empty(), mapResult);
  }

  @Test
  void onUnmappedItems_when_pojoSchema_then_onSchemasCalled() {
    final PojoSchema pojoSchema =
        new PojoSchema(PojoName.ofNameAndSuffix("Schema", "Dto"), new StringSchema());
    final UnmappedItems unmappedItems = UnmappedItems.ofPojoSchema(pojoSchema);

    final BiFunction<UnmappedItems, NonEmptyList<OpenApiSpec>, MapResult> onSpecifications =
        (newUnmappedItems, openApiSpecs) -> {
          throw new IllegalStateException("onSpecifications called");
        };
    final BiFunction<UnmappedItems, NonEmptyList<PojoSchema>, MapResult> onSchemas =
        (newUnmappedItems, schemas) -> {
          assertEquals(NonEmptyList.single(pojoSchema), schemas);
          assertEquals(PList.empty(), newUnmappedItems.getPojoSchemas());
          return MapResult.empty();
        };
    final Supplier<MapResult> onNoItemUnmapped =
        () -> {
          throw new IllegalStateException("onNoItemUnmapped called");
        };

    // method call
    final MapResult mapResult =
        assertDoesNotThrow(
            () -> unmappedItems.onUnmappedItems(onSpecifications, onSchemas, onNoItemUnmapped));

    assertEquals(MapResult.empty(), mapResult);
  }
}
