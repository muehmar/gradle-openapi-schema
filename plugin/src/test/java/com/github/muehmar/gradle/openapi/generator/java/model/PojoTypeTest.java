package com.github.muehmar.gradle.openapi.generator.java.model;

import static com.github.muehmar.gradle.openapi.generator.model.name.PojoNames.pojoName;
import static org.junit.jupiter.api.Assertions.*;

import com.github.muehmar.gradle.openapi.generator.model.PropertyScope;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PojoTypeTest {
  @ParameterizedTest
  @CsvSource({"REQUEST,NameRequestDto", "RESPONSE,NameResponseDto", "DEFAULT,NameDto"})
  void mapName_when_applied_then_nameMappedCorrectly(PojoType type, String expectedName) {
    final PojoName mappedName = type.mapName(pojoName("Name", "Dto"));

    assertEquals(expectedName, mappedName.asString());
  }

  @ParameterizedTest
  @CsvSource({"READ_ONLY,false", "WRITE_ONLY,true", "DEFAULT,true"})
  void includesPropertyScope_when_allScopes_then_correctFlag(
      PropertyScope scope, boolean expectedIncludesScope) {
    assertEquals(expectedIncludesScope, PojoType.REQUEST.includesPropertyScope(scope));
  }
}
