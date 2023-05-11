package com.github.muehmar.gradle.openapi.generator.model.schema;

import static com.github.muehmar.gradle.openapi.generator.model.schema.MapToMemberTypeTestUtil.mapToMemberType;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.PropertyCount;
import com.github.muehmar.gradle.openapi.generator.model.pojo.MapPojo;
import com.github.muehmar.gradle.openapi.generator.model.type.AnyType;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class MapSchemaTest {
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
        MapContext.ofPojo(
            MapPojo.of(
                pojoName,
                "",
                ObjectType.ofName(PojoName.ofName(Name.ofString("Gender"))),
                Constraints.ofPropertiesCount(PropertyCount.ofMinAndMaxProperties(4, 7))));

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

    final PojoName objectPojoName = PojoName.ofNameAndSuffix("MapObject", "");
    final PojoSchema pojoSchema = new PojoSchema(objectPojoName, objectSchema);

    final MapContext expectedContext =
        MapContext.ofPojo(
                MapPojo.of(
                    pojoName,
                    "",
                    ObjectType.ofName(objectPojoName),
                    Constraints.ofPropertiesCount(PropertyCount.ofMaxProperties(12))))
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
        MapContext.ofPojo(
            MapPojo.of(
                pojoName,
                "",
                AnyType.create(),
                Constraints.ofPropertiesCount(PropertyCount.ofMinProperties(2))));

    assertEquals(expectedContext, mapContext);
  }
}
