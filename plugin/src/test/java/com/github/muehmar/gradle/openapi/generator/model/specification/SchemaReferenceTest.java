package com.github.muehmar.gradle.openapi.generator.model.specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SchemaReferenceTest {

  @ParameterizedTest
  @MethodSource("validReferences")
  void fromRefString_when_differentReferences_then_correctParsed(
      String ref, Optional<OpenApiSpec> remoteSpec, Name schemaName) {
    final SchemaReference schemaReference = SchemaReference.fromRefString(ref);
    assertEquals(remoteSpec, schemaReference.getRemoteSpec());
    assertEquals(schemaName, schemaReference.getSchemaName());
  }

  public static Stream<Arguments> validReferences() {
    return Stream.of(
        Arguments.arguments("#/components/schemas/User", Optional.empty(), Name.ofString("User")),
        Arguments.arguments(
            "../schema.yml#/components/schemas/User",
            Optional.of(OpenApiSpec.fromString("../schema.yml")),
            Name.ofString("User")),
        Arguments.arguments(
            "schema.yml#/components/schemas/user",
            Optional.of(OpenApiSpec.fromString("schema.yml")),
            Name.ofString("User")));
  }

  @ParameterizedTest
  @MethodSource("invalidReferences")
  void fromRefString_when_invalidReferences_then_throws(String ref) {
    assertThrows(IllegalArgumentException.class, () -> SchemaReference.fromRefString(ref));
  }

  public static Stream<Arguments> invalidReferences() {
    return Stream.of(
        Arguments.arguments("http://server-xyz.com/to/your/resource.json#/components/schemas/User"),
        Arguments.arguments("../schema.yml"));
  }
}
