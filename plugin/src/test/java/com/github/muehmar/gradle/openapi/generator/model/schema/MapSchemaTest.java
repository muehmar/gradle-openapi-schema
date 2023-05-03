package com.github.muehmar.gradle.openapi.generator.model.schema;

import static com.github.muehmar.gradle.openapi.generator.model.schema.MapToMemberTypeTestUtil.mapToMemberType;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

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
  void mapToMemberType_when_mapSchemaAndObjectSchemaAsAdditionalProperties_then_correctType() {
    final MapSchema mapSchema = new MapSchema();
    final io.swagger.v3.oas.models.media.ObjectSchema objectSchema = new ObjectSchema();
    final HashMap<String, Schema> properties = new HashMap<>();
    properties.put("name", new io.swagger.v3.oas.models.media.StringSchema());
    objectSchema.setProperties(properties);
    mapSchema.setAdditionalProperties(objectSchema);

    final MemberSchemaMapResult result =
        mapToMemberType(
            PojoName.ofName(Name.ofString("invoice")), Name.ofString("page"), mapSchema);

    final PojoName invoicePagePojoName = PojoName.ofName(Name.ofString("InvoicePage"));
    final MapType expectedType =
        MapType.ofKeyAndValueType(StringType.noFormat(), ObjectType.ofName(invoicePagePojoName));

    assertEquals(expectedType, result.getType());
    assertEquals(
        UnmappedItems.ofPojoSchema(new PojoSchema(invoicePagePojoName, objectSchema)),
        result.getUnmappedItems());
  }

  @Test
  void mapToMemberType_when_mapSchemaAndStringSchemaAsAdditionalProperties_then_correctType() {
    final MapSchema mapSchema = new MapSchema();
    final io.swagger.v3.oas.models.media.StringSchema stringSchema = new StringSchema();
    stringSchema.format("url");
    mapSchema.setAdditionalProperties(stringSchema);

    final MemberSchemaMapResult result = mapToMemberType(mapSchema);

    final MapType expectedType =
        MapType.ofKeyAndValueType(
            StringType.noFormat(), StringType.ofFormat(StringType.Format.URL));

    assertEquals(expectedType, result.getType());
    assertEquals(UnmappedItems.empty(), result.getUnmappedItems());
  }
}
