package com.github.muehmar.gradle.openapi.generator.java.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class JavaIdentifierTest {

  @ParameterizedTest
  @MethodSource("originalNamesAndExpectedNames")
  void test(String original, String expected) {
    JavaIdentifier javaIdentifier = JavaIdentifier.fromString(original);
    assertEquals(expected, javaIdentifier.asString());
  }

  public static Stream<Arguments> originalNamesAndExpectedNames() {
    return Stream.of(
        arguments("fieldName", "fieldName"),
        arguments("fieldName2", "fieldName2"),
        arguments("$fieldName", "$fieldName"),
        arguments("field-name", "field_name"),
        arguments("2field-name", "_2field_name"),
        arguments("field2-name", "field2_name"),
        arguments("field-%name", "field_name"),
        arguments("new", "new_"),
        arguments("New", "New"),
        arguments("public", "public_"),
        arguments("Public", "Public"));
  }
}
