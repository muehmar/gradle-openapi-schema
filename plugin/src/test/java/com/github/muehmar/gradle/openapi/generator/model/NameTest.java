package com.github.muehmar.gradle.openapi.generator.model;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class NameTest {

  @Test
  void startUpperCase_when_startsLowercase_then_startsUppercase() {
    final Name field = Name.ofString("field").startUpperCase();
    assertEquals("Field", field.asString());
  }

  @Test
  void startUpperCase_when_startsUppercase_then_isEquals() {
    final Name field = Name.ofString("Field");
    assertEquals(field, field.startUpperCase());
  }

  @ParameterizedTest
  @CsvSource({"java.lang.String,String", "GenderDto,String"})
  void contains_when_called_then_resultIsEqualsToDirectStringOperation(
      String name, String sequence) {
    final Name nameInstance = Name.ofString(name);

    assertEquals(nameInstance.contains(sequence), name.contains(sequence));
  }

  @ParameterizedTest
  @CsvSource({"java.lang.String,String", "GenderDto,Gender"})
  void startsNotWith_when_called_then_resultIsEqualsToDirectStringOperation(
      String name, String sequence) {
    final Name nameInstance = Name.ofString(name);

    assertEquals(nameInstance.startsNotWith(sequence), not(name.startsWith(sequence)));
  }
}
