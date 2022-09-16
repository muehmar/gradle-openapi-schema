package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
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

class MapSchemaMapperTest extends BaseTypeMapperTest {

  @Test
  void mapThrowing_when_mapSchemaWithReferenceInAdditionalProperties_then_correctType() {
    final MapSchema mapSchema = new MapSchema();
    final Schema<Object> additionalProperties = new Schema<>();
    additionalProperties.$ref("#/components/schemas/gender");
    mapSchema.setAdditionalProperties(additionalProperties);

    final MemberSchemaMapResult result = run(mapSchema);

    final MapType expectedType =
        MapType.ofKeyAndValueType(
            StringType.noFormat(), ObjectType.ofName(PojoName.ofName(Name.ofString("Gender"))));

    assertEquals(expectedType, result.getType());
    assertEquals(PList.empty(), result.getPojoSchemas());
  }

  @Test
  void mapThrowing_when_mapSchemaAndObjectSchemaAsAdditionalProperties_then_correctType() {
    final MapSchema mapSchema = new MapSchema();
    final ObjectSchema objectSchema = new ObjectSchema();
    final HashMap<String, Schema> properties = new HashMap<>();
    properties.put("name", new StringSchema());
    objectSchema.setProperties(properties);
    mapSchema.setAdditionalProperties(objectSchema);

    final MemberSchemaMapResult result =
        run(PojoName.ofName(Name.ofString("invoice")), Name.ofString("page"), mapSchema);

    final PojoName invoicePagePojoName = PojoName.ofName(Name.ofString("InvoicePage"));
    final MapType expectedType =
        MapType.ofKeyAndValueType(StringType.noFormat(), ObjectType.ofName(invoicePagePojoName));

    assertEquals(expectedType, result.getType());
    assertEquals(
        PList.single(new PojoSchema(invoicePagePojoName, objectSchema)), result.getPojoSchemas());
  }

  @Test
  void mapThrowing_when_mapSchemaAndStringSchemaAsAdditionalProperties_then_correctType() {
    final MapSchema mapSchema = new MapSchema();
    final StringSchema stringSchema = new StringSchema();
    stringSchema.format("url");
    mapSchema.setAdditionalProperties(stringSchema);

    final MemberSchemaMapResult result = run(mapSchema);

    final MapType expectedType =
        MapType.ofKeyAndValueType(
            StringType.noFormat(), StringType.ofFormat(StringType.Format.URL));

    assertEquals(expectedType, result.getType());
    assertEquals(PList.empty(), result.getPojoSchemas());
  }
}
