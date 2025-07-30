package com.github.muehmar.gradle.openapi.generator.mapper;

import static com.github.muehmar.gradle.openapi.generator.model.name.ComponentNames.componentName;
import static com.github.muehmar.gradle.openapi.generator.model.schema.SchemaWrappers.wrap;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.ParameterSchema;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.pojo.EnumPojo;
import com.github.muehmar.gradle.openapi.generator.model.schema.OpenApiSchema;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.nio.file.Paths;
import java.util.function.BiFunction;
import org.junit.jupiter.api.Test;

class MapContextTest {
  @Test
  void onUnmappedItems_when_noUnmappedItems_then_resultReturned() {
    final UnmappedItems unmappedItems = UnmappedItems.empty();
    final MapContext mapContext =
        MapContext.fromUnmappedItemsAndResult(unmappedItems, UnresolvedMapResult.empty());

    final BiFunction<MapContext, NonEmptyList<OpenApiSpec>, UnresolvedMapResult> onSpecifications =
        (ctx, openApiSpecs) -> {
          throw new IllegalStateException("onSpecifications called");
        };
    final BiFunction<MapContext, NonEmptyList<PojoSchema>, UnresolvedMapResult> onSchemas =
        (ctx, schemas) -> {
          throw new IllegalStateException("onSchemas called");
        };
    final BiFunction<MapContext, NonEmptyList<ParameterSchema>, UnresolvedMapResult> onParameters =
        (ctx, parameters) -> {
          throw new IllegalStateException("onSchemas called");
        };

    // method call
    final UnresolvedMapResult unresolvedMapResult =
        assertDoesNotThrow(
            () -> mapContext.onUnmappedItems(onSpecifications, onSchemas, onParameters));

    assertEquals(UnresolvedMapResult.empty(), unresolvedMapResult);
  }

  @Test
  void onUnmappedItems_when_openApiSpec_then_onSpecificationsCalledAndUsedSpecAddedToResult() {
    final OpenApiSpec spec = OpenApiSpec.fromPath(Paths.get("../components.yml"));
    final UnmappedItems unmappedItems = UnmappedItems.ofSpec(spec);
    final UnresolvedMapResult returnedUnresolvedMapResult =
        UnresolvedMapResult.ofPojo(
            EnumPojo.of(componentName("Enum", "Dto"), "Desc", PList.of("member1", "member2")));
    final MapContext mapContext =
        MapContext.fromUnmappedItemsAndResult(unmappedItems, UnresolvedMapResult.empty());

    final BiFunction<MapContext, NonEmptyList<OpenApiSpec>, UnresolvedMapResult> onSpecifications =
        (ctx, openApiSpecs) -> {
          assertEquals(NonEmptyList.single(spec), openApiSpecs);
          assertEquals(UnmappedItems.empty(), ctx.getUnmappedItems());
          return returnedUnresolvedMapResult;
        };
    final BiFunction<MapContext, NonEmptyList<PojoSchema>, UnresolvedMapResult> onSchemas =
        (ctx, schemas) -> {
          throw new IllegalStateException("onSchemas called");
        };
    final BiFunction<MapContext, NonEmptyList<ParameterSchema>, UnresolvedMapResult> onParameters =
        (ctx, parameters) -> {
          throw new IllegalStateException("onSchemas called");
        };

    // method call
    final UnresolvedMapResult unresolvedMapResult =
        assertDoesNotThrow(
            () -> mapContext.onUnmappedItems(onSpecifications, onSchemas, onParameters));

    final UnresolvedMapResult expectedUnresolvedMapResult =
        returnedUnresolvedMapResult.merge(UnresolvedMapResult.ofUsedSpecs(PList.single(spec)));
    assertEquals(expectedUnresolvedMapResult, unresolvedMapResult);
  }

  @Test
  void onUnmappedItems_when_pojoSchema_then_onSchemasCalled() {
    final UnresolvedMapResult expectedUnresolvedMapResult =
        UnresolvedMapResult.ofPojo(
            EnumPojo.of(componentName("Enum", "Dto"), "Desc", PList.of("member1", "member2")));
    final PojoSchema pojoSchema =
        new PojoSchema(
            componentName("Schema", "Dto"), OpenApiSchema.wrapSchema(wrap(new StringSchema())));
    final UnmappedItems unmappedItems = UnmappedItems.ofPojoSchema(pojoSchema);
    final MapContext mapContext =
        MapContext.fromUnmappedItemsAndResult(unmappedItems, UnresolvedMapResult.empty());

    final BiFunction<MapContext, NonEmptyList<OpenApiSpec>, UnresolvedMapResult> onSpecifications =
        (ctx, openApiSpecs) -> {
          throw new IllegalStateException("onSpecifications called");
        };
    final BiFunction<MapContext, NonEmptyList<PojoSchema>, UnresolvedMapResult> onSchemas =
        (ctx, schemas) -> {
          assertEquals(NonEmptyList.single(pojoSchema), schemas);
          assertEquals(UnmappedItems.empty(), ctx.getUnmappedItems());
          return expectedUnresolvedMapResult;
        };
    final BiFunction<MapContext, NonEmptyList<ParameterSchema>, UnresolvedMapResult> onParameters =
        (ctx, parameters) -> {
          throw new IllegalStateException("onSchemas called");
        };

    // method call
    final UnresolvedMapResult unresolvedMapResult =
        assertDoesNotThrow(
            () -> mapContext.onUnmappedItems(onSpecifications, onSchemas, onParameters));

    assertEquals(expectedUnresolvedMapResult, unresolvedMapResult);
  }

  @Test
  void onUnmappedItems_when_parameterSchema_then_onParametersCalled() {
    final ParameterSchema parameterSchema =
        new ParameterSchema(
            Name.ofString("limitParam"), OpenApiSchema.wrapSchema(wrap(new IntegerSchema())));
    final UnmappedItems unmappedItems = UnmappedItems.ofParameterSchema(parameterSchema);
    final MapContext mapContext =
        MapContext.fromUnmappedItemsAndResult(unmappedItems, UnresolvedMapResult.empty());

    final BiFunction<MapContext, NonEmptyList<OpenApiSpec>, UnresolvedMapResult> onSpecifications =
        (ctx, openApiSpecs) -> {
          throw new IllegalStateException("onSpecifications called");
        };
    final BiFunction<MapContext, NonEmptyList<PojoSchema>, UnresolvedMapResult> onSchemas =
        (ctx, schemas) -> {
          throw new IllegalStateException("onSchemas called");
        };
    final BiFunction<MapContext, NonEmptyList<ParameterSchema>, UnresolvedMapResult> onParameters =
        (ctx, parameters) -> {
          assertEquals(NonEmptyList.single(parameterSchema), parameters);
          assertEquals(UnmappedItems.empty(), ctx.getUnmappedItems());
          return UnresolvedMapResult.empty();
        };

    // method call
    final UnresolvedMapResult unresolvedMapResult =
        assertDoesNotThrow(
            () -> mapContext.onUnmappedItems(onSpecifications, onSchemas, onParameters));

    assertEquals(UnresolvedMapResult.empty(), unresolvedMapResult);
  }
}
