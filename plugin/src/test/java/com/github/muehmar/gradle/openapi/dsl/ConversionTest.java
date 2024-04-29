package com.github.muehmar.gradle.openapi.dsl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.exception.OpenApiGeneratorException;
import com.github.muehmar.gradle.openapi.generator.settings.TypeConversion;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ConversionTest {

  @ParameterizedTest
  @CsvSource({",value", "value,"})
  void assertValidConfig_when_notAllValuesSet_then_throwsOpenApiGeneratorException(
      String fromCustomType, String toCustomType) {
    final Conversion conversion = new Conversion();
    conversion.setFromCustomType(fromCustomType);
    conversion.setToCustomType(toCustomType);

    assertThrows(OpenApiGeneratorException.class, () -> conversion.assertValidConfig(""));
  }

  @ParameterizedTest
  @CsvSource({",", "value,value"})
  void assertValidConfig_when_eitherBothOrNoValueSet_then_doesNotThrow(
      String fromCustomType, String toCustomType) {
    final Conversion conversion = new Conversion();
    conversion.setFromCustomType(fromCustomType);
    conversion.setToCustomType(toCustomType);

    assertDoesNotThrow(() -> conversion.assertValidConfig(""));
  }

  @ParameterizedTest
  @CsvSource({",", ",value", "value,"})
  void toTypeConversion_when_notAllValuesSet_then_returnedEmpty(
      String fromCustomType, String toCustomType) {
    final Conversion conversion = new Conversion();
    conversion.setFromCustomType(fromCustomType);
    conversion.setToCustomType(toCustomType);

    final Optional<TypeConversion> typeConversion = conversion.toTypeConversion();

    assertEquals(Optional.empty(), typeConversion);
  }

  @Test
  void toTypeConversion_when_bothValuesSet_then_returnNonEmpty() {
    final Conversion conversion = new Conversion();
    conversion.setFromCustomType("value");
    conversion.setToCustomType("value");

    final Optional<TypeConversion> typeConversion = conversion.toTypeConversion();

    assertTrue(typeConversion.isPresent());
  }
}
