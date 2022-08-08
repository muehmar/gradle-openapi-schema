package com.github.muehmar.gradle.openapi.generator.java.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import org.junit.jupiter.api.Test;

class ReferenceMapperTest {

  @Test
  void getRefName_when_startLowercaseReference_then_refNameStartsUppercase() {
    final Name refName = ReferenceMapper.getRefName("#/components/schemas/gender");
    assertEquals("Gender", refName.asString());
  }
}
