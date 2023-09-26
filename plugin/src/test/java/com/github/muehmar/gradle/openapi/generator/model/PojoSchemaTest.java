package com.github.muehmar.gradle.openapi.generator.model;

import static com.github.muehmar.gradle.openapi.generator.model.name.ComponentNames.componentName;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.Test;

class PojoSchemaTest {

  @Test
  void getPojoName_when_instanceConstructedWithStartingLowercaseName_then_nameStartsUppercase() {
    final PojoSchema pojoSchema = new PojoSchema(componentName("gender", "Dto"), new Schema<>());
    assertEquals("GenderDto", pojoSchema.getPojoName().asString());
  }
}
