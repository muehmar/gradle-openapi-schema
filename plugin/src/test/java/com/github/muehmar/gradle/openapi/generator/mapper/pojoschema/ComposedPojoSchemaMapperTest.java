package com.github.muehmar.gradle.openapi.generator.mapper.pojoschema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.mapper.UnresolvedMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.UnresolvedComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.HashMap;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ComposedPojoSchemaMapperTest {
  private static final ComposedPojoSchemaMapper COMPOSED_POJO_SCHEMA_MAPPER =
      new ComposedPojoSchemaMapper();

  @Test
  void map_when_allOfSchemaAndLocalAndRemoteReferences_then_correctMapContext() {
    final ComposedSchema composedSchema = new ComposedSchema();
    composedSchema.setDescription("Test description");
    composedSchema.addAllOfItem(
        new Schema<>().$ref("../../dir/components.yml#/components/schemas/ReferenceSchema1"));
    composedSchema.addAllOfItem(new Schema<>().$ref("#/components/schemas/ReferenceSchema2"));

    final PojoName pojoName = PojoName.ofNameAndSuffix(Name.ofString("ComposedPojoName"), "Dto");
    final PojoSchema pojoSchema = new PojoSchema(pojoName, composedSchema);

    // method call
    final Optional<MapContext> result = COMPOSED_POJO_SCHEMA_MAPPER.map(pojoSchema);

    assertTrue(result.isPresent());
    final MapContext mapContext = result.get();

    final UnresolvedMapResult unresolvedMapResult = mapContext.getUnresolvedMapResult();
    assertEquals(0, unresolvedMapResult.getPojos().size());
    assertEquals(1, unresolvedMapResult.getUnresolvedComposedPojos().size());
    assertEquals(0, unresolvedMapResult.getPojoMemberReferences().size());

    final UnresolvedComposedPojo expectedComposedPojo =
        new UnresolvedComposedPojo(
            pojoName,
            "Test description",
            UnresolvedComposedPojo.CompositionType.ALL_OF,
            PList.of(
                PojoName.ofNameAndSuffix("ReferenceSchema1", "Dto"),
                PojoName.ofNameAndSuffix("ReferenceSchema2", "Dto")),
            Optional.empty());
    assertEquals(expectedComposedPojo, unresolvedMapResult.getUnresolvedComposedPojos().apply(0));
    assertEquals(
        UnmappedItems.ofSpec(OpenApiSpec.fromString("../../dir/components.yml")),
        mapContext.getUnmappedItems());
  }

  @Test
  void map_when_singleInlineDefinition_then_correctMapContext() {
    final ComposedSchema composedSchema = new ComposedSchema();
    composedSchema.setDescription("Test description");

    final ObjectSchema objectSchema = new ObjectSchema();
    final HashMap<String, Schema> properties = new HashMap<>();
    properties.put("key", new StringSchema());
    objectSchema.setProperties(properties);
    composedSchema.addOneOfItem(objectSchema);

    final PojoName pojoName = PojoName.ofNameAndSuffix(Name.ofString("ComposedPojoName"), "Dto");
    final PojoSchema pojoSchema = new PojoSchema(pojoName, composedSchema);

    // method call
    final Optional<MapContext> result = COMPOSED_POJO_SCHEMA_MAPPER.map(pojoSchema);

    assertTrue(result.isPresent());
    final MapContext mapContext = result.get();

    final UnresolvedMapResult unresolvedMapResult = mapContext.getUnresolvedMapResult();
    assertEquals(0, unresolvedMapResult.getPojos().size());
    assertEquals(1, unresolvedMapResult.getUnresolvedComposedPojos().size());
    assertEquals(0, unresolvedMapResult.getPojoMemberReferences().size());

    final PojoName objectSchemaPojoName = PojoName.ofNameAndSuffix("ComposedPojoNameOneOf", "Dto");
    final UnresolvedComposedPojo expectedComposedPojo =
        new UnresolvedComposedPojo(
            pojoName,
            "Test description",
            UnresolvedComposedPojo.CompositionType.ONE_OF,
            PList.of(objectSchemaPojoName),
            Optional.empty());
    assertEquals(expectedComposedPojo, unresolvedMapResult.getUnresolvedComposedPojos().apply(0));
    assertEquals(
        UnmappedItems.ofPojoSchema(new PojoSchema(objectSchemaPojoName, objectSchema)),
        mapContext.getUnmappedItems());
  }

  @Test
  void map_when_multipleInlineDefinition_then_correctNumeratedPojosCreated() {
    final ComposedSchema composedSchema = new ComposedSchema();
    composedSchema.setDescription("Test description");

    final ObjectSchema objectSchema1 = new ObjectSchema();
    final HashMap<String, Schema> properties1 = new HashMap<>();
    properties1.put("key1", new StringSchema());
    objectSchema1.setProperties(properties1);

    final ObjectSchema objectSchema2 = new ObjectSchema();
    final HashMap<String, Schema> properties2 = new HashMap<>();
    properties2.put("key2", new IntegerSchema());
    objectSchema2.setProperties(properties2);

    composedSchema.addOneOfItem(objectSchema1);
    composedSchema.addOneOfItem(objectSchema2);

    final PojoName pojoName = PojoName.ofNameAndSuffix(Name.ofString("ComposedPojoName"), "Dto");
    final PojoSchema pojoSchema = new PojoSchema(pojoName, composedSchema);

    // method call
    final Optional<MapContext> result = COMPOSED_POJO_SCHEMA_MAPPER.map(pojoSchema);

    assertTrue(result.isPresent());
    final MapContext mapContext = result.get();

    final UnresolvedMapResult unresolvedMapResult = mapContext.getUnresolvedMapResult();
    assertEquals(0, unresolvedMapResult.getPojos().size());
    assertEquals(1, unresolvedMapResult.getUnresolvedComposedPojos().size());
    assertEquals(0, unresolvedMapResult.getPojoMemberReferences().size());

    final PojoName objectSchema1PojoName =
        PojoName.ofNameAndSuffix("ComposedPojoNameOneOf0", "Dto");
    final PojoName objectSchema2PojoName =
        PojoName.ofNameAndSuffix("ComposedPojoNameOneOf1", "Dto");
    final UnresolvedComposedPojo expectedComposedPojo =
        new UnresolvedComposedPojo(
            pojoName,
            "Test description",
            UnresolvedComposedPojo.CompositionType.ONE_OF,
            PList.of(objectSchema1PojoName, objectSchema2PojoName),
            Optional.empty());
    assertEquals(expectedComposedPojo, unresolvedMapResult.getUnresolvedComposedPojos().apply(0));
    assertEquals(
        UnmappedItems.empty()
            .addPojoSchemas(
                PList.of(
                    new PojoSchema(objectSchema1PojoName, objectSchema1),
                    new PojoSchema(objectSchema2PojoName, objectSchema2))),
        mapContext.getUnmappedItems());
  }
}
