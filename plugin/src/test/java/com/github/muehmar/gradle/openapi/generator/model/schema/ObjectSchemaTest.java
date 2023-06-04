package com.github.muehmar.gradle.openapi.generator.model.schema;

import static com.github.muehmar.gradle.openapi.generator.model.AdditionalProperties.anyTypeAllowed;
import static com.github.muehmar.gradle.openapi.generator.model.schema.MapToMemberTypeTestUtil.mapToMemberType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.mapper.UnresolvedMapResult;
import com.github.muehmar.gradle.openapi.generator.model.AdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.PropertyScope;
import com.github.muehmar.gradle.openapi.generator.model.UnresolvedObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.UnresolvedObjectPojoBuilder;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.PropertyCount;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojoBuilder;
import com.github.muehmar.gradle.openapi.generator.model.type.AnyType;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
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
    final PojoName pojoName = PojoName.ofName(Name.ofString("Person"));
    final Name memberName = Name.ofString("Address");
    final Schema<?> schema = new ObjectSchema();
    final HashMap<String, Schema> properties = new HashMap<>();
    properties.put("street", new StringSchema());
    schema.setProperties(properties);

    final MemberSchemaMapResult result = mapToMemberType(pojoName, memberName, schema);

    final PojoName expectedPojoName = PojoName.deriveOpenApiPojoName(pojoName, memberName);
    assertEquals(ObjectType.ofName(expectedPojoName), result.getType());
    assertEquals(
        UnmappedItems.ofPojoSchema(new PojoSchema(expectedPojoName, schema)),
        result.getUnmappedItems());
  }

  @Test
  void mapToPojo_when_schemaWithInlineDefinitionAndReference_then_correctPojoCreated() {
    final ObjectSchema objectSchema = new ObjectSchema();
    objectSchema.setDescription("Test description");

    final HashMap<String, Schema> properties = new HashMap<>();
    properties.put("stringVal", new StringSchema());

    final ObjectSchema objectSchemaProp = new ObjectSchema();
    final HashMap<String, Schema> objectSchemaPropProperties = new HashMap<>();
    objectSchemaPropProperties.put("intVal", new io.swagger.v3.oas.models.media.IntegerSchema());
    objectSchemaProp.setProperties(objectSchemaPropProperties);

    properties.put("objectVal", objectSchemaProp);
    properties.put("refVal", new Schema<>().$ref("#/components/schemas/ReferenceSchema1"));
    objectSchema.setProperties(properties);

    final PojoName pojoName = PojoName.ofNameAndSuffix(Name.ofString("Object"), "Dto");
    final PojoSchema pojoSchema = new PojoSchema(pojoName, objectSchema);

    // method call
    final MapContext mapContext = pojoSchema.mapToPojo();

    final UnresolvedMapResult unresolvedMapResult = mapContext.getUnresolvedMapResult();
    assertEquals(0, unresolvedMapResult.getPojos().size());
    assertEquals(1, unresolvedMapResult.getUnresolvedObjectPojos().size());
    assertEquals(0, unresolvedMapResult.getPojoMemberReferences().size());

    final ObjectPojo objectPojo =
        resolveUncomposedObjectPojo(unresolvedMapResult.getUnresolvedObjectPojos().apply(0));

    final PojoName memberObjectPojoName = PojoName.ofNameAndSuffix("ObjectObjectVal", "Dto");

    final ObjectPojo expectedPojo =
        ObjectPojoBuilder.create()
            .name(pojoName)
            .description("Test description")
            .members(
                PList.of(
                    new PojoMember(
                        Name.ofString("objectVal"),
                        null,
                        ObjectType.ofName(memberObjectPojoName),
                        PropertyScope.DEFAULT,
                        Necessity.OPTIONAL,
                        Nullability.NOT_NULLABLE),
                    new PojoMember(
                        Name.ofString("stringVal"),
                        null,
                        StringType.noFormat(),
                        PropertyScope.DEFAULT,
                        Necessity.OPTIONAL,
                        Nullability.NOT_NULLABLE),
                    new PojoMember(
                        Name.ofString("refVal"),
                        null,
                        ObjectType.ofName(PojoName.ofNameAndSuffix("ReferenceSchema1", "Dto")),
                        PropertyScope.DEFAULT,
                        Necessity.OPTIONAL,
                        Nullability.NOT_NULLABLE)))
            .constraints(Constraints.empty())
            .additionalProperties(anyTypeAllowed())
            .build();

    assertEquals(expectedPojo, objectPojo);
    assertEquals(
        UnmappedItems.ofPojoSchema(new PojoSchema(memberObjectPojoName, objectSchemaProp)),
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

    final PojoName pojoName = PojoName.ofNameAndSuffix(Name.ofString("Object"), "Dto");
    final PojoSchema pojoSchema = new PojoSchema(pojoName, objectSchema);

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
                PropertyScope.DEFAULT,
                Necessity.OPTIONAL,
                Nullability.NULLABLE),
            new PojoMember(
                Name.ofString("numVal"),
                null,
                NumericType.formatFloat(),
                PropertyScope.DEFAULT,
                Necessity.REQUIRED,
                Nullability.NULLABLE),
            new PojoMember(
                Name.ofString("stringVal"),
                null,
                StringType.noFormat(),
                PropertyScope.DEFAULT,
                Necessity.REQUIRED,
                Nullability.NOT_NULLABLE));
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

    final PojoName pojoName = PojoName.ofNameAndSuffix(Name.ofString("Object"), "Dto");
    final PojoSchema pojoSchema = new PojoSchema(pojoName, objectSchema);

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
                Nullability.NOT_NULLABLE),
            new PojoMember(
                Name.ofString("numVal"),
                null,
                NumericType.formatFloat(),
                PropertyScope.DEFAULT,
                Necessity.OPTIONAL,
                Nullability.NOT_NULLABLE),
            new PojoMember(
                Name.ofString("stringVal"),
                null,
                StringType.noFormat(),
                PropertyScope.READ_ONLY,
                Necessity.OPTIONAL,
                Nullability.NOT_NULLABLE));
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

    final PojoName pojoName = PojoName.ofNameAndSuffix(Name.ofString("Object"), "Dto");
    final PojoSchema pojoSchema = new PojoSchema(pojoName, objectSchema);

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
                Name.ofString("otherVal"),
                "Additional Property 'otherVal'",
                AnyType.create(),
                PropertyScope.DEFAULT,
                Necessity.REQUIRED,
                Nullability.NOT_NULLABLE),
            new PojoMember(
                Name.ofString("stringVal"),
                "",
                StringType.noFormat(),
                PropertyScope.DEFAULT,
                Necessity.REQUIRED,
                Nullability.NOT_NULLABLE));
    assertEquals(
        expectedMembers,
        objectPojo.getMembers().sort(Comparator.comparing(member -> member.getName().asString())));
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

    final PojoName pojoName = PojoName.ofNameAndSuffix(Name.ofString("Object"), "Dto");
    final PojoSchema pojoSchema = new PojoSchema(pojoName, objectSchema);

    // method call
    final MapContext mapContext = pojoSchema.mapToPojo();

    final UnresolvedMapResult unresolvedMapResult = mapContext.getUnresolvedMapResult();
    assertEquals(0, unresolvedMapResult.getPojos().size());
    assertEquals(1, unresolvedMapResult.getUnresolvedObjectPojos().size());
    assertEquals(0, unresolvedMapResult.getPojoMemberReferences().size());

    final ObjectPojo objectPojo =
        resolveUncomposedObjectPojo(unresolvedMapResult.getUnresolvedObjectPojos().apply(0));

    final PojoName additionalPropertiesPojoName = PojoName.ofNameAndSuffix("ObjectProperty", "Dto");
    final PList<PojoMember> expectedMembers =
        PList.of(
            new PojoMember(
                Name.ofString("gender"),
                "Additional Property 'gender'",
                ObjectType.ofName(additionalPropertiesPojoName),
                PropertyScope.DEFAULT,
                Necessity.REQUIRED,
                Nullability.NOT_NULLABLE));
    assertEquals(
        expectedMembers,
        objectPojo.getMembers().sort(Comparator.comparing(member -> member.getName().asString())));

    final UnmappedItems expectedUnmappedItems =
        UnmappedItems.ofPojoSchema(
            new PojoSchema(additionalPropertiesPojoName, additionalPropertiesSchema));
    assertEquals(expectedUnmappedItems, mapContext.getUnmappedItems());
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
            StringType.noFormat(), ObjectType.ofName(PojoName.ofName(Name.ofString("Gender"))));

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

    final PojoName pojoName = PojoName.ofName(Name.ofString("Map"));
    final MapContext mapContext = OpenApiSchema.wrapSchema(mapSchema).mapToPojo(pojoName);

    final MapContext expectedContext =
        MapContext.ofUnresolvedObjectPojo(
            UnresolvedObjectPojoBuilder.create()
                .name(pojoName)
                .description("")
                .members(PList.empty())
                .constraints(
                    Constraints.ofPropertiesCount(PropertyCount.ofMinAndMaxProperties(4, 7)))
                .additionalProperties(
                    AdditionalProperties.allowed(
                        ObjectType.ofName(PojoName.ofName(Name.ofString("Gender")))))
                .build());

    assertEquals(expectedContext, mapContext);
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

    final MemberSchemaMapResult result =
        mapToMemberType(
            PojoName.ofName(Name.ofString("invoice")), Name.ofString("page"), mapSchema);

    final PojoName invoicePagePojoName = PojoName.ofName(Name.ofString("InvoicePage"));
    final MapType expectedType =
        MapType.ofKeyAndValueType(StringType.noFormat(), ObjectType.ofName(invoicePagePojoName))
            .withConstraints(Constraints.ofPropertiesCount(PropertyCount.ofMaxProperties(5)));

    assertEquals(expectedType, result.getType());
    assertEquals(
        UnmappedItems.ofPojoSchema(new PojoSchema(invoicePagePojoName, objectSchema)),
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

    final PojoName pojoName = PojoName.ofName(Name.ofString("Map"));
    final MapContext mapContext = OpenApiSchema.wrapSchema(mapSchema).mapToPojo(pojoName);

    final PojoName objectPojoName = PojoName.ofNameAndSuffix("MapProperty", "");
    final PojoSchema pojoSchema = new PojoSchema(objectPojoName, objectSchema);

    final MapContext expectedContext =
        MapContext.ofUnresolvedObjectPojo(
                UnresolvedObjectPojoBuilder.create()
                    .name(pojoName)
                    .description("")
                    .members(PList.empty())
                    .constraints(Constraints.ofPropertiesCount(PropertyCount.ofMaxProperties(12)))
                    .additionalProperties(
                        AdditionalProperties.allowed(ObjectType.ofName(objectPojoName)))
                    .build())
            .addUnmappedItems(UnmappedItems.ofPojoSchema(pojoSchema));

    assertEquals(expectedContext, mapContext);
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
        MapType.ofKeyAndValueType(StringType.noFormat(), AnyType.create())
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

    final PojoName pojoName = PojoName.ofName(Name.ofString("Map"));
    final MapContext mapContext = OpenApiSchema.wrapSchema(mapSchema).mapToPojo(pojoName);

    final MapContext expectedContext =
        MapContext.ofUnresolvedObjectPojo(
            UnresolvedObjectPojoBuilder.create()
                .name(pojoName)
                .description("")
                .members(PList.empty())
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
