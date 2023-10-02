package com.github.muehmar.gradle.openapi.generator.java.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

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
        arguments("field_%name", "field_name"),
        arguments("field%_name", "field_name"),
        arguments("__fieldname", "__fieldname"),
        arguments("field__name", "field__name"),
        arguments("new", "new_"),
        arguments("New", "New"),
        arguments("public", "public_"),
        arguments("Public", "Public"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"name", "other, name, other", "name, name"})
  void wordBoundaryPattern_when_usedAsPattern_then_matchesTexts(String text) {
    final JavaIdentifier javaIdentifier = JavaIdentifier.fromString("name");
    final Pattern pattern = Pattern.compile(javaIdentifier.wordBoundaryPattern());
    assertTrue(pattern.matcher(text).find());
  }

  @ParameterizedTest
  @ValueSource(strings = {"namename", "surname", "names"})
  void wordBoundaryPattern_when_usedAsPattern_then_doesNotMatchesTexts(String text) {
    final JavaIdentifier javaIdentifier = JavaIdentifier.fromString("name");
    final Pattern pattern = Pattern.compile(javaIdentifier.wordBoundaryPattern());
    assertFalse(pattern.matcher(text).find());
  }
}
