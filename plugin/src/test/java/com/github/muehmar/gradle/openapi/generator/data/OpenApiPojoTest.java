package com.github.muehmar.gradle.openapi.generator.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.Test;

class OpenApiPojoTest {

  @Test
  void getName_when_instanceConstructedWithStartingLowercaseName_then_nameStartsUppercase() {
    final OpenApiPojo openApiPojo = new OpenApiPojo(Name.of("gender"), new Schema<>());
    assertEquals("Gender", openApiPojo.getName().asString());
  }
}
