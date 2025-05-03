package com.github.muehmar.gradle.openapi.generator.model.schema;

import static com.github.muehmar.gradle.openapi.generator.model.AdditionalProperties.anyTypeAllowed;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.UnresolvedObjectPojoBuilder.unresolvedObjectPojoBuilder;
import static com.github.muehmar.gradle.openapi.generator.model.name.ComponentNames.componentName;
import static com.github.muehmar.gradle.openapi.generator.model.name.PojoNames.pojoName;
import static com.github.muehmar.gradle.openapi.generator.model.schema.MapToMemberTypeTestUtil.mapToMemberType;
import static com.github.muehmar.gradle.openapi.generator.model.type.IntegerType.Format.INTEGER;
import static com.github.muehmar.gradle.openapi.generator.model.type.NumericType.Format.FLOAT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.mapper.UnresolvedMapResult;
import com.github.muehmar.gradle.openapi.generator.model.*;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.PropertyCount;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojoBuilder;
import com.github.muehmar.gradle.openapi.generator.model.type.AnyType;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StandardObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class ObjectSchemaTest {

  @Test
  void mapToMemberType_when_uuidSchema_then_correctType() {
    final ComponentName componentName = componentName("Person", "");
    final Name memberName = Name.ofString("Address");
    final Schema<?> schema = new ObjectSchema();
    final HashMap<String, Schema> properties = new HashMap<>();
    properties.put("street", new StringSchema());
    schema.setProperties(properties);

    final MemberSchemaMapResult result = mapToMemberType(componentName, memberName, schema);

    final ComponentName expectedPojoName = componentName.deriveMemberSchemaName(memberName);
    assertEquals(StandardObjectType.ofName(expectedPojoName.getPojoName()), result.getType());
    assertEquals(
        UnmappedItems.ofPojoSchema(new PojoSchema(expectedPojoName, schema)),
        result.getUnmappedItems());
  }

  @Test
  void mapToMemberType_when_nullableFlagIsTrue_then_objectTypeIsNullable() {
    final ComponentName componentName = componentName("Person", "");
    final Name memberName = Name.ofString("Address");
    final Schema<?> schema = new ObjectSchema();
    schema.setNullable(true);

    final MemberSchemaMapResult result = mapToMemberType(componentName, memberName, schema);

    assertEquals(NULLABLE, result.getType().getNullability());
  }

  @Test
  void mapToPojo_when_schemaWithInlineDefinitionAndReference_then_correctPojoCreated() {
    final ObjectSchema objectSchema = new ObjectSchema();
    objectSchema.setDescription("Test description");
    objectSchema.setNullable(true);

    final HashMap<String, Schema> properties = new HashMap<>();
    properties.put("stringVal", new StringSchema());

    final ObjectSchema objectSchemaProp = new ObjectSchema();
    final HashMap<String, Schema> objectSchemaPropProperties = new HashMap<>();
    objectSchemaPropProperties.put("intVal", new io.swagger.v3.oas.models.media.IntegerSchema());
    objectSchemaProp.setProperties(objectSchemaPropProperties);

    properties.put("objectVal", objectSchemaProp);
    properties.put("refVal", new Schema<>().$ref("#/components/schemas/ReferenceSchema1"));
    objectSchema.setProperties(properties);

    final ComponentName componentName = componentName("Object", "Dto");
    final PojoSchema pojoSchema = new PojoSchema(componentName, objectSchema);

    // method call
    final MapContext mapContext = pojoSchema.mapToPojo();

    final UnresolvedMapResult unresolvedMapResult = mapContext.getUnresolvedMapResult();
    assertEquals(0, unresolvedMapResult.getPojos().size());
    assertEquals(1, unresolvedMapResult.getUnresolvedObjectPojos().size());
    assertEquals(0, unresolvedMapResult.getPojoMemberReferences().size());

    final ObjectPojo objectPojo =
        resolveUncomposedObjectPojo(unresolvedMapResult.getUnresolvedObjectPojos().apply(0));

    final ComponentName memberObjectComponentName =
        componentName.deriveMemberSchemaName(Name.ofString("objectVal"));

    final ObjectPojo expectedPojo =
        ObjectPojoBuilder.create()
            .name(componentName)
            .description("Test description")
            .nullability(NULLABLE)
            .pojoXml(PojoXml.noXmlDefinition())
            .members(
                PList.of(
                    new PojoMember(
                        Name.ofString("objectVal"),
                        null,
                        StandardObjectType.ofName(memberObjectComponentName.getPojoName()),
                        PropertyScope.DEFAULT,
                        Necessity.OPTIONAL,
                        PojoMemberXml.noDefinition()),
                    new PojoMember(
                        Name.ofString("stringVal"),
                        null,
                        StringType.noFormat(),
                        PropertyScope.DEFAULT,
                        Necessity.OPTIONAL,
                        PojoMemberXml.noDefinition()),
                    new PojoMember(
                        Name.ofString("refVal"),
                        null,
                        StandardObjectType.ofName(pojoName("ReferenceSchema1", "Dto")),
                        PropertyScope.DEFAULT,
                        Necessity.OPTIONAL,
                        PojoMemberXml.noDefinition())))
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.empty())
            .additionalProperties(anyTypeAllowed())
            .build();

    assertEquals(expectedPojo, objectPojo);
    assertEquals(
        UnmappedItems.ofPojoSchema(new PojoSchema(memberObjectComponentName, objectSchemaProp)),
        mapContext.getUnmappedItems());
  }

  @Test
  void mapToPojo_when_schemaWithRequiredAndNullable_then_correctPojoMemberCreated() {
    final ObjectSchema objectSchema = new ObjectSchema();
    objectSchema.setDescription("Test description");

    final HashMap<String, Schema> properties = new HashMap<>();
    properties.put("stringVal", new StringSchema());
    properties.put("intVal", new io.swagger.v3.oas.models.media.IntegerSchema().nullable(true));
    properties.put("numVal", new io.swagger.v3.oas.models.media.NumberSchema().nullable(true));
    objectSchema.setProperties(properties);
    objectSchema.setRequired(Arrays.asList("stringVal", "numVal"));

    final ComponentName componentName = componentName("Object", "Dto");
    final PojoSchema pojoSchema = new PojoSchema(componentName, objectSchema);

    // method call
    final MapContext mapContext = pojoSchema.mapToPojo();

    final UnresolvedMapResult unresolvedMapResult = mapContext.getUnresolvedMapResult();
    assertEquals(0, unresolvedMapResult.getPojos().size());
    assertEquals(1, unresolvedMapResult.getUnresolvedObjectPojos().size());
    assertEquals(0, unresolvedMapResult.getPojoMemberReferences().size());

    final ObjectPojo objectPojo =
        resolveUncomposedObjectPojo(unresolvedMapResult.getUnresolvedObjectPojos().apply(0));

    final PList<PojoMember> expectedMembers =
        PList.of(
            new PojoMember(
                Name.ofString("intVal"),
                null,
                IntegerType.ofFormat(INTEGER, NULLABLE),
                PropertyScope.DEFAULT,
                Necessity.OPTIONAL,
                PojoMemberXml.noDefinition()),
            new PojoMember(
                Name.ofString("numVal"),
                null,
                NumericType.ofFormat(FLOAT, NULLABLE),
                PropertyScope.DEFAULT,
                Necessity.REQUIRED,
                PojoMemberXml.noDefinition()),
            new PojoMember(
                Name.ofString("stringVal"),
                null,
                StringType.noFormat(),
                PropertyScope.DEFAULT,
                Necessity.REQUIRED,
                PojoMemberXml.noDefinition()));
    assertEquals(
        expectedMembers,
        objectPojo.getMembers().sort(Comparator.comparing(member -> member.getName().asString())));
    assertEquals(UnmappedItems.empty(), mapContext.getUnmappedItems());
  }

  @Test
  void mapToPojo_when_schemaWithReadOnlyAndWriteOnly_then_correctPojoMemberCreated() {
    final ObjectSchema objectSchema = new ObjectSchema();
    objectSchema.setDescription("Test description");

    final HashMap<String, Schema> properties = new HashMap<>();
    properties.put("stringVal", new StringSchema().readOnly(true));
    properties.put("intVal", new IntegerSchema().writeOnly(true));
    properties.put("numVal", new NumberSchema());
    objectSchema.setProperties(properties);

    final ComponentName componentName = componentName("Object", "Dto");
    final PojoSchema pojoSchema = new PojoSchema(componentName, objectSchema);

    // method call
    final MapContext mapContext = pojoSchema.mapToPojo();

    final UnresolvedMapResult unresolvedMapResult = mapContext.getUnresolvedMapResult();
    assertEquals(0, unresolvedMapResult.getPojos().size());
    assertEquals(1, unresolvedMapResult.getUnresolvedObjectPojos().size());
    assertEquals(0, unresolvedMapResult.getPojoMemberReferences().size());

    final ObjectPojo objectPojo =
        resolveUncomposedObjectPojo(unresolvedMapResult.getUnresolvedObjectPojos().apply(0));

    final PList<PojoMember> expectedMembers =
        PList.of(
            new PojoMember(
                Name.ofString("intVal"),
                null,
                IntegerType.formatInteger(),
                PropertyScope.WRITE_ONLY,
                Necessity.OPTIONAL,
                PojoMemberXml.noDefinition()),
            new PojoMember(
                Name.ofString("numVal"),
                null,
                NumericType.formatFloat(),
                PropertyScope.DEFAULT,
                Necessity.OPTIONAL,
                PojoMemberXml.noDefinition()),
            new PojoMember(
                Name.ofString("stringVal"),
                null,
                StringType.noFormat(),
                PropertyScope.READ_ONLY,
                Necessity.OPTIONAL,
                PojoMemberXml.noDefinition()));
    assertEquals(
        expectedMembers,
        objectPojo.getMembers().sort(Comparator.comparing(member -> member.getName().asString())));
    assertEquals(UnmappedItems.empty(), mapContext.getUnmappedItems());
  }

  @Test
  void mapToPojo_when_schemaWithRequiredAdditionalProperties_then_correctMembers() {
    final ObjectSchema objectSchema = new ObjectSchema();
    objectSchema.setDescription("Test description");

    final HashMap<String, Schema> properties = new HashMap<>();
    properties.put("stringVal", new StringSchema());
    objectSchema.setProperties(properties);
    objectSchema.required(Arrays.asList("stringVal", "otherVal"));

    final ComponentName componentName = componentName("Object", "Dto");
    final PojoSchema pojoSchema = new PojoSchema(componentName, objectSchema);

    // method call
    final MapContext mapContext = pojoSchema.mapToPojo();

    final UnresolvedMapResult unresolvedMapResult = mapContext.getUnresolvedMapResult();
    assertEquals(0, unresolvedMapResult.getPojos().size());
    assertEquals(1, unresolvedMapResult.getUnresolvedObjectPojos().size());
    assertEquals(0, unresolvedMapResult.getPojoMemberReferences().size());

    final ObjectPojo objectPojo =
        resolveUncomposedObjectPojo(unresolvedMapResult.getUnresolvedObjectPojos().apply(0));

    final PList<PojoMember> expectedMembers =
        PList.of(
            new PojoMember(
                Name.ofString("stringVal"),
                "",
                StringType.noFormat(),
                PropertyScope.DEFAULT,
                Necessity.REQUIRED,
                PojoMemberXml.noDefinition()));
    assertEquals(
        expectedMembers,
        objectPojo.getMembers().sort(Comparator.comparing(member -> member.getName().asString())));
    assertEquals(
        PList.single(Name.ofString("otherVal")), objectPojo.getRequiredAdditionalProperties());
    assertEquals(UnmappedItems.empty(), mapContext.getUnmappedItems());
  }

  @Test
  void mapToPojo_when_additionalPropertiesSchemaIsObjectSchema_then_correctUnmappedItems() {
    final ObjectSchema objectSchema = new ObjectSchema();
    objectSchema.setProperties(Collections.emptyMap());
    objectSchema.setDescription("Test description");

    final Schema<Object> additionalPropertiesSchema = new ObjectSchema();
    additionalPropertiesSchema.required(Collections.singletonList("other"));
    objectSchema.required(Collections.singletonList("gender"));
    objectSchema.setAdditionalProperties(additionalPropertiesSchema);

    final ComponentName componentName = componentName("Object", "Dto");
    final PojoSchema pojoSchema = new PojoSchema(componentName, objectSchema);

    // method call
    final MapContext mapContext = pojoSchema.mapToPojo();

    final UnresolvedMapResult unresolvedMapResult = mapContext.getUnresolvedMapResult();
    assertEquals(0, unresolvedMapResult.getPojos().size());
    assertEquals(1, unresolvedMapResult.getUnresolvedObjectPojos().size());
    assertEquals(0, unresolvedMapResult.getPojoMemberReferences().size());

    final ObjectPojo objectPojo =
        resolveUncomposedObjectPojo(unresolvedMapResult.getUnresolvedObjectPojos().apply(0));

    final ComponentName additionalPropertiesName =
        componentName.deriveMemberSchemaName(Name.ofString("Property"));

    assertEquals(
        PList.single(Name.ofString("gender")), objectPojo.getRequiredAdditionalProperties());

    final UnmappedItems expectedUnmappedItems =
        UnmappedItems.ofPojoSchema(
            new PojoSchema(additionalPropertiesName, additionalPropertiesSchema));
    assertEquals(expectedUnmappedItems, mapContext.getUnmappedItems());
  }

  @Test
  void mapToPojo_when_schemaWithOnlyRequiredProperties_then_objectSchemaDetected() {
    final Schema<Object> objectSchema = new Schema<>();
    objectSchema.setRequired(Collections.singletonList("name"));
    objectSchema.setDescription("Test description");

    final ComponentName componentName = componentName("Object", "Dto");
    final PojoSchema pojoSchema = new PojoSchema(componentName, objectSchema);

    // method call
    final MapContext mapContext = pojoSchema.mapToPojo();

    final UnresolvedMapResult unresolvedMapResult = mapContext.getUnresolvedMapResult();
    assertEquals(0, unresolvedMapResult.getPojos().size());
    assertEquals(1, unresolvedMapResult.getUnresolvedObjectPojos().size());
    assertEquals(0, unresolvedMapResult.getPojoMemberReferences().size());
  }

  @Test
  void mapToMemberType_when_mapSchemaWithReferenceInAdditionalProperties_then_correctType() {
    final MapSchema mapSchema = new MapSchema();
    final Schema<Object> additionalProperties = new Schema<>();
    additionalProperties.$ref("#/components/schemas/gender");
    mapSchema.setAdditionalProperties(additionalProperties);

    final MemberSchemaMapResult result = mapToMemberType(mapSchema);

    final MapType expectedType =
        MapType.ofKeyAndValueType(
            StringType.noFormat(),
            StandardObjectType.ofName(PojoName.ofName(Name.ofString("Gender"))));

    assertEquals(expectedType, result.getType());
    assertEquals(UnmappedItems.empty(), result.getUnmappedItems());
  }

  @Test
  void mapToPojo_when_mapSchemaWithReferenceInAdditionalProperties_then_correctPojo() {
    final MapSchema mapSchema = new MapSchema();
    final Schema<Object> additionalProperties = new Schema<>();
    additionalProperties.$ref("#/components/schemas/gender");
    mapSchema.setAdditionalProperties(additionalProperties);
    mapSchema.setMinProperties(4);
    mapSchema.setMaxProperties(7);

    final ComponentName componentName = componentName("Map", "");
    final MapContext mapContext = OpenApiSchema.wrapSchema(mapSchema).mapToPojo(componentName);

    final MapContext expectedContext =
        MapContext.ofUnresolvedObjectPojo(
            UnresolvedObjectPojoBuilder.create()
                .name(componentName)
                .description("")
                .nullability(NOT_NULLABLE)
                .pojoXml(PojoXml.noXmlDefinition())
                .members(PList.empty())
                .requiredAdditionalProperties(PList.empty())
                .constraints(
                    Constraints.ofPropertiesCount(PropertyCount.ofMinAndMaxProperties(4, 7)))
                .additionalProperties(
                    AdditionalProperties.allowed(
                        StandardObjectType.ofName(PojoName.ofName(Name.ofString("Gender")))))
                .build());

    assertEquals(expectedContext, mapContext);
  }

  @Test
  void mapToPojo_when_schemaWithXmlAttributesForProperty_then_objectSchemaDetected() {
    final Schema<Object> objectSchema = new Schema<>();

    final HashMap<String, Schema> properties = new HashMap<>();
    final StringSchema stringSchema = new StringSchema();
    final XML xml = new XML();
    xml.setAttribute(true);
    xml.setName("xml-name");
    stringSchema.setXml(xml);
    properties.put("stringVal", stringSchema);
    objectSchema.setProperties(properties);

    final ComponentName componentName = componentName("Object", "Dto");
    final PojoSchema pojoSchema = new PojoSchema(componentName, objectSchema);

    // method call
    final MapContext mapContext = pojoSchema.mapToPojo();

    final UnresolvedMapResult unresolvedMapResult = mapContext.getUnresolvedMapResult();
    assertEquals(0, unresolvedMapResult.getPojos().size());
    assertEquals(1, unresolvedMapResult.getUnresolvedObjectPojos().size());
    assertEquals(0, unresolvedMapResult.getPojoMemberReferences().size());

    final ObjectPojo objectPojo =
        resolveUncomposedObjectPojo(unresolvedMapResult.getUnresolvedObjectPojos().apply(0));

    final PList<PojoMember> expectedMembers =
        PList.of(
            new PojoMember(
                Name.ofString("stringVal"),
                "",
                StringType.noFormat(),
                PropertyScope.DEFAULT,
                Necessity.OPTIONAL,
                new PojoMemberXml(Optional.of("xml-name"), Optional.of(true))));
    assertEquals(
        expectedMembers,
        objectPojo.getMembers().sort(Comparator.comparing(member -> member.getName().asString())));
  }

  @Test
  void mapToMemberType_when_mapSchemaAndObjectSchemaAsAdditionalProperties_then_correctType() {
    final ObjectSchema objectSchema = new ObjectSchema();
    final HashMap<String, Schema> properties = new HashMap<>();
    properties.put("name", new StringSchema());
    objectSchema.setProperties(properties);

    final MapSchema mapSchema = new MapSchema();
    mapSchema.setAdditionalProperties(objectSchema);
    mapSchema.maxProperties(5);

    final ComponentName componentName = componentName("invoice", "");
    final Name pojoMemberName = Name.ofString("page");
    final MemberSchemaMapResult result = mapToMemberType(componentName, pojoMemberName, mapSchema);

    final ComponentName invoicePageComponentName =
        componentName.deriveMemberSchemaName(Name.ofString("page"));
    final MapType expectedType =
        MapType.ofKeyAndValueType(
                StringType.noFormat(),
                StandardObjectType.ofName(invoicePageComponentName.getPojoName()))
            .withConstraints(Constraints.ofPropertiesCount(PropertyCount.ofMaxProperties(5)));

    assertEquals(expectedType, result.getType());
    assertEquals(
        UnmappedItems.ofPojoSchema(new PojoSchema(invoicePageComponentName, objectSchema)),
        result.getUnmappedItems());
  }

  @Test
  void mapToPojo_when_mapSchemaAndObjectSchemaAsAdditionalProperties_then_correctPojo() {
    final ObjectSchema objectSchema = new ObjectSchema();
    final HashMap<String, Schema> properties = new HashMap<>();
    properties.put("name", new StringSchema());
    objectSchema.setProperties(properties);

    final MapSchema mapSchema = new MapSchema();
    mapSchema.setAdditionalProperties(objectSchema);
    mapSchema.maxProperties(12);

    final ComponentName componentName = componentName("Map", "");
    final MapContext mapContext = OpenApiSchema.wrapSchema(mapSchema).mapToPojo(componentName);

    final ComponentName objectComponentName =
        componentName.deriveMemberSchemaName(Name.ofString("Property"));
    final PojoSchema pojoSchema = new PojoSchema(objectComponentName, objectSchema);

    final MapContext expectedContext =
        MapContext.ofUnresolvedObjectPojo(
                unresolvedObjectPojoBuilder()
                    .name(componentName)
                    .description("")
                    .nullability(NOT_NULLABLE)
                    .pojoXml(PojoXml.noXmlDefinition())
                    .members(PList.empty())
                    .requiredAdditionalProperties(PList.empty())
                    .constraints(Constraints.ofPropertiesCount(PropertyCount.ofMaxProperties(12)))
                    .additionalProperties(
                        AdditionalProperties.allowed(
                            StandardObjectType.ofName(objectComponentName.getPojoName())))
                    .build())
            .addUnmappedItems(UnmappedItems.ofPojoSchema(pojoSchema));

    assertEquals(expectedContext, mapContext);
  }

  @Test
  void mapToMemberType_when_mapSchemaAndNullableFlagIsTrue_then_nullableMapType() {
    final MapSchema mapSchema = new MapSchema();
    mapSchema.setNullable(true);
    final StringSchema stringSchema = new StringSchema();
    mapSchema.setAdditionalProperties(stringSchema);

    final MemberSchemaMapResult result = mapToMemberType(mapSchema);

    assertInstanceOf(MapType.class, result.getType());
    assertEquals(NULLABLE, result.getType().getNullability());
  }

  @Test
  void mapToMemberType_when_mapSchemaAndStringSchemaAsAdditionalProperties_then_correctType() {
    final MapSchema mapSchema = new MapSchema();
    final StringSchema stringSchema = new StringSchema();
    stringSchema.format("url");
    mapSchema.setAdditionalProperties(stringSchema);

    final MemberSchemaMapResult result = mapToMemberType(mapSchema);

    final MapType expectedType =
        MapType.ofKeyAndValueType(
            StringType.noFormat(), StringType.ofFormat(StringType.Format.URL));

    assertEquals(expectedType, result.getType());
    assertEquals(UnmappedItems.empty(), result.getUnmappedItems());
  }

  @Test
  void mapToMemberType_when_anyOfSchema_then_correctTypeAndUnmappedItem() {
    final ComposedSchema composedSchema = new ComposedSchema();
    composedSchema.setAllOf(
        Collections.singletonList(new Schema<>().$ref("#/components/schemas/ReferenceSchema1")));

    final ComponentName componentName = componentName("ComposedPojo", "Dto");
    final Name pojoMemberName = Name.ofString("Member");
    final MemberSchemaMapResult result =
        mapToMemberType(componentName, pojoMemberName, composedSchema);

    final ComponentName expectedComponentName =
        componentName.deriveMemberSchemaName(pojoMemberName);
    final ObjectType expectedType = StandardObjectType.ofName(expectedComponentName.getPojoName());

    assertEquals(expectedType, result.getType());
    assertEquals(
        UnmappedItems.ofPojoSchema(new PojoSchema(expectedComponentName, composedSchema)),
        result.getUnmappedItems());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true})
  @NullSource
  void mapToMemberType_when_additionalPropertiesAreNullOrTrue_then_correctMapResult(
      Boolean additionalProperties) {
    final MapSchema mapSchema = new MapSchema();
    mapSchema.setAdditionalProperties(additionalProperties);
    mapSchema.minProperties(9);

    final MemberSchemaMapResult result = mapToMemberType(mapSchema);

    final MapType mapType =
        MapType.ofKeyAndValueType(StringType.noFormat(), AnyType.create(NULLABLE))
            .withConstraints(Constraints.ofPropertiesCount(PropertyCount.ofMinProperties(9)));

    final MemberSchemaMapResult memberSchemaMapResult = MemberSchemaMapResult.ofType(mapType);

    assertEquals(memberSchemaMapResult, result);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true})
  @NullSource
  void mapToPojo_when_additionalPropertiesAreNullOrTrue_then_correctMapResult(
      Boolean additionalProperties) {
    final MapSchema mapSchema = new MapSchema();
    mapSchema.setAdditionalProperties(additionalProperties);
    mapSchema.minProperties(2);

    final ComponentName componentName = componentName("Map", "");
    final MapContext mapContext = OpenApiSchema.wrapSchema(mapSchema).mapToPojo(componentName);

    final MapContext expectedContext =
        MapContext.ofUnresolvedObjectPojo(
            UnresolvedObjectPojoBuilder.create()
                .name(componentName)
                .description("")
                .nullability(NOT_NULLABLE)
                .pojoXml(PojoXml.noXmlDefinition())
                .members(PList.empty())
                .requiredAdditionalProperties(PList.empty())
                .constraints(Constraints.ofPropertiesCount(PropertyCount.ofMinProperties(2)))
                .additionalProperties(AdditionalProperties.anyTypeAllowed())
                .build());

    assertEquals(expectedContext, mapContext);
  }

  private static ObjectPojo resolveUncomposedObjectPojo(UnresolvedObjectPojo unresolvedObjectPojo) {
    final Optional<ObjectPojo> result =
        unresolvedObjectPojo.resolve(
            ignore -> Optional.empty(), ignore -> Optional.empty(), ignore -> Optional.empty());
    assertTrue(result.isPresent());
    return result.get();
  }
}
