package com.github.muehmar.gradle.openapi.generator.mapper;

import static com.github.muehmar.gradle.openapi.generator.model.name.ComponentNames.componentName;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.ParameterSchema;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.StringSchema;
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
    final BiFunction<UnmappedItems, NonEmptyList<ParameterSchema>, UnresolvedMapResult>
        onParameters =
            (unmappedItems, parameters) -> {
              throw new IllegalStateException("onParameters called");
            };
    final Supplier<UnresolvedMapResult> onNoItemUnmapped = UnresolvedMapResult::empty;

    // method call
    final UnresolvedMapResult unresolvedMapResult =
        assertDoesNotThrow(
            () ->
                empty.onUnmappedItems(onSpecifications, onSchemas, onParameters, onNoItemUnmapped));

    assertEquals(UnresolvedMapResult.empty(), unresolvedMapResult);
  }

  @Test
  void onUnmappedItems_when_openApiSpec_then_onSpecificationsCalled() {
    final OpenApiSpec spec = OpenApiSpec.fromString("../components-yml");
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
    final BiFunction<UnmappedItems, NonEmptyList<ParameterSchema>, UnresolvedMapResult>
        onParameters =
            (newUnmappedItems, parameters) -> {
              throw new IllegalStateException("onParameters called");
            };
    final Supplier<UnresolvedMapResult> onNoItemUnmapped =
        () -> {
          throw new IllegalStateException("onNoItemUnmapped called");
        };

    // method call
    final UnresolvedMapResult unresolvedMapResult =
        assertDoesNotThrow(
            () ->
                unmappedItems.onUnmappedItems(
                    onSpecifications, onSchemas, onParameters, onNoItemUnmapped));

    assertEquals(UnresolvedMapResult.empty(), unresolvedMapResult);
  }

  @Test
  void onUnmappedItems_when_pojoSchema_then_onSchemasCalled() {
    final PojoSchema pojoSchema =
        new PojoSchema(componentName("Schema", "Dto"), new StringSchema());
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
    final BiFunction<UnmappedItems, NonEmptyList<ParameterSchema>, UnresolvedMapResult>
        onParameters =
            (newUnmappedItems, parameters) -> {
              throw new IllegalStateException("onParameters called");
            };
    final Supplier<UnresolvedMapResult> onNoItemUnmapped =
        () -> {
          throw new IllegalStateException("onNoItemUnmapped called");
        };

    // method call
    final UnresolvedMapResult unresolvedMapResult =
        assertDoesNotThrow(
            () ->
                unmappedItems.onUnmappedItems(
                    onSpecifications, onSchemas, onParameters, onNoItemUnmapped));

    assertEquals(UnresolvedMapResult.empty(), unresolvedMapResult);
  }

  @Test
  void onUnmappedItems_when_parameterSchema_then_onParametersCalled() {
    final ParameterSchema parameterSchema =
        new ParameterSchema(Name.ofString("limitParam"), new IntegerSchema());
    final UnmappedItems unmappedItems = UnmappedItems.ofParameterSchema(parameterSchema);

    final BiFunction<UnmappedItems, NonEmptyList<OpenApiSpec>, UnresolvedMapResult>
        onSpecifications =
            (newUnmappedItems, openApiSpecs) -> {
              throw new IllegalStateException("onSpecifications called");
            };
    final BiFunction<UnmappedItems, NonEmptyList<PojoSchema>, UnresolvedMapResult> onSchemas =
        (newUnmappedItems, schemas) -> {
          throw new IllegalStateException("onSchemas called");
        };
    final BiFunction<UnmappedItems, NonEmptyList<ParameterSchema>, UnresolvedMapResult>
        onParameters =
            (newUnmappedItems, parameters) -> {
              assertEquals(NonEmptyList.single(parameterSchema), parameters);
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
            () ->
                unmappedItems.onUnmappedItems(
                    onSpecifications, onSchemas, onParameters, onNoItemUnmapped));

    assertEquals(UnresolvedMapResult.empty(), unresolvedMapResult);
  }
}
