package com.github.muehmar.gradle.openapi.generator.mapper;

import static com.github.muehmar.gradle.openapi.generator.model.name.ComponentNames.componentName;
import static com.github.muehmar.gradle.openapi.generator.model.schema.SchemaWrappers.wrap;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import io.swagger.v3.oas.models.media.StringSchema;
import java.nio.file.Path;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

class UnmappedItemsTest {
  @Test
  void onUnmappedItems_when_empty_then_resultSupplierCalled() {
    final UnmappedItems empty = UnmappedItems.empty();

    final BiFunction<UnmappedItems, NonEmptyList<OpenApiSpec>, UnresolvedMapResult>
        onSpecifications =
            (unmappedItems, openApiSpecs) -> {
              throw new IllegalStateException("onSpecifications called");
            };
    final BiFunction<UnmappedItems, NonEmptyList<PojoSchema>, UnresolvedMapResult> onSchemas =
        (unmappedItems, schemas) -> {
          throw new IllegalStateException("onSchemas called");
        };
    final Supplier<UnresolvedMapResult> onNoItemUnmapped = UnresolvedMapResult::empty;

    // method call
    final UnresolvedMapResult unresolvedMapResult =
        assertDoesNotThrow(
            () -> empty.onUnmappedItems(onSpecifications, onSchemas, onNoItemUnmapped));

    assertEquals(UnresolvedMapResult.empty(), unresolvedMapResult);
  }

  @Test
  void onUnmappedItems_when_openApiSpec_then_onSpecificationsCalled() {
    final OpenApiSpec spec = OpenApiSpec.fromPath(Path.of("../components-yml"));
    final UnmappedItems unmappedItems = UnmappedItems.ofSpec(spec);

    final BiFunction<UnmappedItems, NonEmptyList<OpenApiSpec>, UnresolvedMapResult>
        onSpecifications =
            (newUnmappedItems, openApiSpecs) -> {
              assertEquals(NonEmptyList.single(spec), openApiSpecs);
              assertEquals(PList.empty(), newUnmappedItems.getSpecifications());
              return UnresolvedMapResult.empty();
            };
    final BiFunction<UnmappedItems, NonEmptyList<PojoSchema>, UnresolvedMapResult> onSchemas =
        (newUnmappedItems, schemas) -> {
          throw new IllegalStateException("onSchemas called");
        };
    final Supplier<UnresolvedMapResult> onNoItemUnmapped =
        () -> {
          throw new IllegalStateException("onNoItemUnmapped called");
        };

    // method call
    final UnresolvedMapResult unresolvedMapResult =
        assertDoesNotThrow(
            () -> unmappedItems.onUnmappedItems(onSpecifications, onSchemas, onNoItemUnmapped));

    assertEquals(UnresolvedMapResult.empty(), unresolvedMapResult);
  }

  @Test
  void onUnmappedItems_when_pojoSchema_then_onSchemasCalled() {
    final PojoSchema pojoSchema =
        new PojoSchema(componentName("Schema", "Dto"), wrap(new StringSchema()));
    final UnmappedItems unmappedItems = UnmappedItems.ofPojoSchema(pojoSchema);

    final BiFunction<UnmappedItems, NonEmptyList<OpenApiSpec>, UnresolvedMapResult>
        onSpecifications =
            (newUnmappedItems, openApiSpecs) -> {
              throw new IllegalStateException("onSpecifications called");
            };
    final BiFunction<UnmappedItems, NonEmptyList<PojoSchema>, UnresolvedMapResult> onSchemas =
        (newUnmappedItems, schemas) -> {
          assertEquals(NonEmptyList.single(pojoSchema), schemas);
          assertEquals(PList.empty(), newUnmappedItems.getPojoSchemas());
          return UnresolvedMapResult.empty();
        };
    final Supplier<UnresolvedMapResult> onNoItemUnmapped =
        () -> {
          throw new IllegalStateException("onNoItemUnmapped called");
        };

    // method call
    final UnresolvedMapResult unresolvedMapResult =
        assertDoesNotThrow(
            () -> unmappedItems.onUnmappedItems(onSpecifications, onSchemas, onNoItemUnmapped));

    assertEquals(UnresolvedMapResult.empty(), unresolvedMapResult);
  }
}
