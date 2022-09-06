package com.github.muehmar.gradle.openapi.generator.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.Test;

class OpenApiPojoTest {

  @Test
  void getName_when_instanceConstructedWithStartingLowercaseName_then_nameStartsUppercase() {
    final OpenApiPojo openApiPojo = new OpenApiPojo(Name.of("gender"), new Schema<>());
    assertEquals("Gender", openApiPojo.getName().asString());
  }

  @Test
  void getPojoName_when_instanceConstructedWithStartingLowercaseName_then_nameStartsUppercase() {
    final OpenApiPojo openApiPojo =
        new OpenApiPojo(PojoName.ofNameAndSuffix("gender", "Dto"), new Schema<>());
    assertEquals("GenderDto", openApiPojo.getPojoName().asString());
  }
}
