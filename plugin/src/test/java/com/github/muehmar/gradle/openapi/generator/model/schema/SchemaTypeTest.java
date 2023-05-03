package com.github.muehmar.gradle.openapi.generator.model.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import io.swagger.v3.oas.models.SpecVersion;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SchemaTypeTest {
  @ParameterizedTest
  @MethodSource("v30SchemaTypes")
  void matchesType_when_v30Schema_then_matchesExpected(
      SchemaType schemaType, String type, boolean expectedMatchesType) {
    final Schema<Object> schema = new Schema<>();
    schema.setSpecVersion(SpecVersion.V30);
    schema.setType(type);

    assertEquals(expectedMatchesType, schemaType.matchesType(schema));
  }

  @ParameterizedTest
  @MethodSource("v30SchemaTypes")
  void isType_when_v30Schema_then_matchesExpected(
      SchemaType schemaType, String type, boolean expectedMatchesType) {
    assertEquals(expectedMatchesType, schemaType.isType(type));
  }

  private static Stream<Arguments> v30SchemaTypes() {
    return Stream.of(
        arguments(SchemaType.STRING, "string", true),
        arguments(SchemaType.STRING, "integer", false),
        arguments(SchemaType.STRING, "", false),
        arguments(SchemaType.STRING, null, false),
        arguments(SchemaType.STRING, "null", false));
  }

  @ParameterizedTest
  @MethodSource("v31SchemaTypes")
  void matchesType_when_v31Schema_then_matchesExpected(
      SchemaType schemaType, String[] types, boolean expectedMatchesType) {
    final Schema<Object> schema = new Schema<>();
    schema.setSpecVersion(SpecVersion.V31);
    schema.setTypes(Stream.of(types).collect(Collectors.toSet()));

    assertEquals(expectedMatchesType, schemaType.matchesType(schema));
  }

  @ParameterizedTest
  @MethodSource("v31SchemaTypes")
  void isSingleType_when_typeSet_then_matchesExpected(
      SchemaType schemaType, String[] types, boolean expectedMatchesType) {
    final Set<String> typesSet = Stream.of(types).collect(Collectors.toSet());

    assertEquals(expectedMatchesType, schemaType.isSingleType(typesSet));
  }

  private static Stream<Arguments> v31SchemaTypes() {
    return Stream.of(
        arguments(SchemaType.STRING, new String[] {"string"}, true),
        arguments(SchemaType.STRING, new String[] {"string", "null"}, true),
        arguments(SchemaType.STRING, new String[] {"string", "null", "integer"}, false),
        arguments(SchemaType.STRING, new String[] {"string", "integer"}, false),
        arguments(SchemaType.STRING, new String[] {"integer"}, false),
        arguments(SchemaType.STRING, new String[] {}, false),
        arguments(SchemaType.STRING, new String[] {"null"}, false),
        arguments(SchemaType.STRING, new String[] {"integer", "null"}, false));
  }
}
