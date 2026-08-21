package com.github.muehmar.gradle.openapi.generator.model;

import static com.github.muehmar.gradle.openapi.generator.model.schema.SchemaWrappers.wrap;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.media.XML;
import java.util.HashMap;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PojoMemberXmlTest {

  @Test
  void fromSchema_when_arraySchema_then_correctXml() {
    final HashMap<String, Schema> properties = new HashMap<>();
    final ArraySchema arraySchema = new ArraySchema();
    final StringSchema itemSchema = new StringSchema();
    final XML itemXml = new XML();
    itemXml.setName("item-name");
    itemSchema.setXml(itemXml);
    arraySchema.setItems(itemSchema);
    final XML arrayXml = new XML();
    arrayXml.setWrapped(true);
    arrayXml.setName("array-name");
    arraySchema.setXml(arrayXml);
    properties.put("array", arraySchema);

    final PojoMemberXml pojoMemberXml = PojoMemberXml.fromSchema(wrap(arraySchema));
    final PojoMemberXml expectedPojoMemberXml =
        new PojoMemberXml(
            Optional.of("array-name"),
            Optional.empty(),
            Optional.of(
                new PojoMemberXml.ArrayXml(
                    Optional.of("array-name"), Optional.of(true), Optional.of("item-name"))));

    assertEquals(expectedPojoMemberXml, pojoMemberXml);
  }

  @Test
  void fromSchema_when_arraySchemaWithoutXml_then_correctXml() {
    final HashMap<String, Schema> properties = new HashMap<>();
    final ArraySchema arraySchema = new ArraySchema();
    final StringSchema itemSchema = new StringSchema();
    arraySchema.setItems(itemSchema);
    properties.put("array", arraySchema);

    final PojoMemberXml pojoMemberXml = PojoMemberXml.fromSchema(wrap(arraySchema));

    assertEquals(PojoMemberXml.noDefinition(), pojoMemberXml);
  }

  @Test
  void fromSchema_when_propertyToAttribute_then_correctXml() {
    final StringSchema stringSchema = new StringSchema();
    final XML xml = new XML();
    xml.setName("string-name");
    xml.setAttribute(true);
    stringSchema.setXml(xml);

    final PojoMemberXml pojoMemberXml = PojoMemberXml.fromSchema(wrap(stringSchema));
    final PojoMemberXml expectedPojoMemberXml =
        new PojoMemberXml(Optional.of("string-name"), Optional.of(true), Optional.empty());

    assertEquals(expectedPojoMemberXml, pojoMemberXml);
  }
}
