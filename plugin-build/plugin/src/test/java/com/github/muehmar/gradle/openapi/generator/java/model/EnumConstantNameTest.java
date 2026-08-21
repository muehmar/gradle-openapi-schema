package com.github.muehmar.gradle.openapi.generator.java.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class EnumConstantNameTest {

  @ParameterizedTest
  @MethodSource("enumConstantNames")
  void asJavaConstant_when_called_then_correctConvertedToJavaConstant(
      String originalConstant, String javaConstant) {
    final EnumConstantName enumConstantName = EnumConstantName.ofString(originalConstant);
    assertEquals(javaConstant, enumConstantName.asJavaConstant().asString());
  }

  public static Stream<Arguments> enumConstantNames() {
    return Stream.of(
        Arguments.of("field:Name", "FIELD_NAME"),
        Arguments.of("*fieldName*", "_FIELD_NAME_"),
        Arguments.of("field**Name", "FIELD_NAME"),
        Arguments.of("123FieldName", "_123_FIELD_NAME"),
        Arguments.of("_123FieldName", "_123_FIELD_NAME"),
        Arguments.of("$123FieldName", "$123_FIELD_NAME"),
        Arguments.of("anyCase", "ANY_CASE"),
        Arguments.of("AnyCase", "ANY_CASE"),
        Arguments.of("anyCase", "ANY_CASE"),
        Arguments.of("ANY_CASE", "ANY_CASE"),
        Arguments.of("Any_Case", "ANY_CASE"));
  }
}
