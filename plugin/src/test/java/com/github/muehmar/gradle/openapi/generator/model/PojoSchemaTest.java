package com.github.muehmar.gradle.openapi.generator.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.Test;

class PojoSchemaTest {

  @Test
  void getName_when_instanceConstructedWithStartingLowercaseName_then_nameStartsUppercase() {
    final PojoSchema pojoSchema = new PojoSchema(Name.ofString("gender"), new Schema<>());
    assertEquals("Gender", pojoSchema.getName().asString());
  }

  @Test
  void getPojoName_when_instanceConstructedWithStartingLowercaseName_then_nameStartsUppercase() {
    final PojoSchema pojoSchema =
        new PojoSchema(PojoName.ofNameAndSuffix("gender", "Dto"), new Schema<>());
    assertEquals("GenderDto", pojoSchema.getPojoName().asString());
  }
}
