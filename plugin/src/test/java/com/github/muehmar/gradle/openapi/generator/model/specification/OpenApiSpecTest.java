package com.github.muehmar.gradle.openapi.generator.model.specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class OpenApiSpecTest {

  @ParameterizedTest
  @MethodSource("specCombinations")
  void fromString_when_combinationsOfCurrentSpecAndNewFile_then_matchExpectedPath(
      String currentSpecPath, String newSpecPath, String expectedPath) {
    final OpenApiSpec currentSpec = OpenApiSpec.fromPath(Path.of(currentSpecPath));
    final OpenApiSpec openApiSpec = OpenApiSpec.fromString(currentSpec, newSpecPath);

    final Path specPath = openApiSpec.asPathWithMainDirectory(MainDirectory.fromPath(Path.of("")));
    assertEquals(expectedPath, specPath.toString());
  }

  static Stream<Arguments> specCombinations() {
    return Stream.of(
        arguments(
            "/specifications/components.yml",
            "./components2.yml",
            "/specifications/components2.yml"),
        arguments("/specifications/components.yml", "../components2.yml", "/components2.yml"),
        arguments("/components.yml", "./components2.yml", "/components2.yml"));
  }
}
