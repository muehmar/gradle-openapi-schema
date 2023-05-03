package com.github.muehmar.gradle.openapi.generator.model.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.mapper.UnresolvedMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.UnresolvedComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.PropertyCount;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ComposedSchemaTest {

  @Test
  void mapToMemberType_when_allOfSchemaAndLocalAndRemoteReferences_then_correctMapContext() {
    final ComposedSchema composedSchema = new ComposedSchema();
    composedSchema.setAllOf(Collections.singletonList(new Schema<>()));

    final PojoName pojoName = PojoName.ofNameAndSuffix(Name.ofString("Pojo"), "Dto");

    // method call
    final MemberSchemaMapResult memberSchemaMapResult =
        MapToMemberTypeTestUtil.mapToMemberType(pojoName, Name.ofString("Member"), composedSchema);

    final PojoName newPojoName = PojoName.ofNameAndSuffix("PojoMember", "Dto");
    assertEquals(ObjectType.ofName(newPojoName), memberSchemaMapResult.getType());

    assertEquals(
        UnmappedItems.ofPojoSchema(
            new PojoSchema(newPojoName, OpenApiSchema.wrapSchema(composedSchema))),
        memberSchemaMapResult.getUnmappedItems());
  }

  @Test
  void mapToPojo_when_singleInlineDefinition_then_correctMapContext() {
    final ComposedSchema composedSchema = new ComposedSchema();
    composedSchema.setDescription("Test description");

    final io.swagger.v3.oas.models.media.ObjectSchema objectSchema =
        new io.swagger.v3.oas.models.media.ObjectSchema();
    final HashMap<String, Schema> properties = new HashMap<>();
    properties.put("key", new io.swagger.v3.oas.models.media.StringSchema());
    objectSchema.setProperties(properties);
    composedSchema.addOneOfItem(objectSchema);
    composedSchema.setMaxProperties(5);

    final PojoName pojoName = PojoName.ofNameAndSuffix(Name.ofString("ComposedPojoName"), "Dto");
    final PojoSchema pojoSchema = new PojoSchema(pojoName, composedSchema);

    // method call
    final MapContext mapContext = pojoSchema.mapToPojo();

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
            Constraints.ofPropertiesCount(PropertyCount.ofMaxProperties(5)),
            Optional.empty());
    assertEquals(expectedComposedPojo, unresolvedMapResult.getUnresolvedComposedPojos().apply(0));
    assertEquals(
        UnmappedItems.ofPojoSchema(new PojoSchema(objectSchemaPojoName, objectSchema)),
        mapContext.getUnmappedItems());
  }

  @Test
  void mapToPojo_when_multipleInlineDefinition_then_correctNumeratedPojosCreated() {
    final ComposedSchema composedSchema = new ComposedSchema();
    composedSchema.setDescription("Test description");

    final io.swagger.v3.oas.models.media.ObjectSchema objectSchema1 =
        new io.swagger.v3.oas.models.media.ObjectSchema();
    final HashMap<String, Schema> properties1 = new HashMap<>();
    properties1.put("key1", new StringSchema());
    objectSchema1.setProperties(properties1);

    final io.swagger.v3.oas.models.media.ObjectSchema objectSchema2 = new ObjectSchema();
    final HashMap<String, Schema> properties2 = new HashMap<>();
    properties2.put("key2", new IntegerSchema());
    objectSchema2.setProperties(properties2);

    composedSchema.addOneOfItem(objectSchema1);
    composedSchema.addOneOfItem(objectSchema2);

    final PojoName pojoName = PojoName.ofNameAndSuffix(Name.ofString("ComposedPojoName"), "Dto");
    final PojoSchema pojoSchema = new PojoSchema(pojoName, composedSchema);

    // method call
    final MapContext mapContext = pojoSchema.mapToPojo();

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
            Constraints.empty(),
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
