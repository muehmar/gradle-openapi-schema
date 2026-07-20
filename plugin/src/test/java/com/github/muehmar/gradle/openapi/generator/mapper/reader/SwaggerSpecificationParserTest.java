package com.github.muehmar.gradle.openapi.generator.mapper.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.ParsedSpecification;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.specification.MainDirectory;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import io.swagger.v3.oas.models.SpecVersion;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import org.gradle.api.GradleException;
import org.junit.jupiter.api.Test;

class SwaggerSpecificationParserTest {
  private static final MainDirectory MAIN_DIRECTORY = MainDirectory.fromString("specs");

  private static final String MAIN_SPEC_V30 =
      "openapi: \"3.0.0\"\n"
          + "info: { }\n"
          + "paths: { }\n"
          + "components:\n"
          + "  schemas:\n"
          + "    Group:\n"
          + "      properties:\n"
          + "        dateFrom:\n"
          + "          $ref: 'schemas.yml#/IsoDate'\n";

  private static final String SCHEMA_ONLY_SPEC =
      "IsoDate:\n"
          + "  type: string\n"
          + "  format: date\n"
          + "Address:\n"
          + "  properties:\n"
          + "    street:\n"
          + "      type: string\n";

  @Test
  void parse_when_referencedFileContainsOnlySchemas_then_allSchemasParsed() {
    final SwaggerSpecificationParser parser =
        parserWithFiles(files("main.yml", MAIN_SPEC_V30, "schemas.yml", SCHEMA_ONLY_SPEC));
    parser.parse(MAIN_DIRECTORY, spec("main.yml"));

    final ParsedSpecification parsed = parser.parse(MAIN_DIRECTORY, spec("schemas.yml"));

    assertEquals(
        PList.of("AddressDto", "IsoDateDto"),
        parsed
            .getPojoSchemas()
            .map(pojoSchema -> pojoSchema.getPojoName().asString())
            .sort(Comparator.naturalOrder()));
  }

  @Test
  void parse_when_referencedFileContainsOnlySchemas_then_specVersionOfMainSpecInherited() {
    final SwaggerSpecificationParser parser =
        parserWithFiles(files("main.yml", MAIN_SPEC_V30, "schemas.yml", SCHEMA_ONLY_SPEC));
    parser.parse(MAIN_DIRECTORY, spec("main.yml"));

    final ParsedSpecification parsed = parser.parse(MAIN_DIRECTORY, spec("schemas.yml"));

    assertEquals(
        PList.of(SpecVersion.V30, SpecVersion.V30),
        parsed
            .getPojoSchemas()
            .map(PojoSchema::getSchema)
            .map(schema -> schema.getDelegateSchema().getSpecVersion()));
  }

  @Test
  void parse_when_mainSpecIsV31_then_specVersionV31InheritedForSchemaOnlyFile() {
    final String mainSpecV31 = MAIN_SPEC_V30.replace("\"3.0.0\"", "\"3.1.0\"");
    final SwaggerSpecificationParser parser =
        parserWithFiles(files("main.yml", mainSpecV31, "schemas.yml", SCHEMA_ONLY_SPEC));
    parser.parse(MAIN_DIRECTORY, spec("main.yml"));

    final ParsedSpecification parsed = parser.parse(MAIN_DIRECTORY, spec("schemas.yml"));

    assertEquals(
        PList.of(SpecVersion.V31, SpecVersion.V31),
        parsed
            .getPojoSchemas()
            .map(PojoSchema::getSchema)
            .map(schema -> schema.getDelegateSchema().getSpecVersion()));
  }

  @Test
  void parse_when_referencedFileContainsComponentsWithoutOpenApiField_then_allSchemasParsed() {
    final String componentsOnlySpec =
        "components:\n"
            + "  schemas:\n"
            + "    Member:\n"
            + "      properties:\n"
            + "        nickname:\n"
            + "          type: string\n";
    final SwaggerSpecificationParser parser =
        parserWithFiles(files("main.yml", MAIN_SPEC_V30, "components.yml", componentsOnlySpec));
    parser.parse(MAIN_DIRECTORY, spec("main.yml"));

    final ParsedSpecification parsed = parser.parse(MAIN_DIRECTORY, spec("components.yml"));

    assertEquals(
        PList.of("MemberDto"),
        parsed.getPojoSchemas().map(pojoSchema -> pojoSchema.getPojoName().asString()));
  }

  @Test
  void parse_when_referencedJsonFileContainsOnlySchemas_then_allSchemasParsed() {
    final String schemaOnlyJson = "{\"IsoDate\": {\"type\": \"string\", \"format\": \"date\"}}";
    final SwaggerSpecificationParser parser =
        parserWithFiles(files("main.yml", MAIN_SPEC_V30, "schemas.json", schemaOnlyJson));
    parser.parse(MAIN_DIRECTORY, spec("main.yml"));

    final ParsedSpecification parsed = parser.parse(MAIN_DIRECTORY, spec("schemas.json"));

    assertEquals(
        PList.of("IsoDateDto"),
        parsed.getPojoSchemas().map(pojoSchema -> pojoSchema.getPojoName().asString()));
  }

  @Test
  void parse_when_mainSpecWithoutOpenApiField_then_throwsWithClearMessage() {
    final SwaggerSpecificationParser parser = parserWithFiles(files("main.yml", SCHEMA_ONLY_SPEC));

    final GradleException exception =
        assertThrows(GradleException.class, () -> parser.parse(MAIN_DIRECTORY, spec("main.yml")));

    assertTrue(exception.getMessage().contains("does not contain the 'openapi' field"));
  }

  @Test
  void parse_when_invalidContent_then_throwsParseError() {
    final SwaggerSpecificationParser parser = parserWithFiles(files("main.yml", "\"unclosed: [ {"));

    assertThrows(GradleException.class, () -> parser.parse(MAIN_DIRECTORY, spec("main.yml")));
  }

  private static OpenApiSpec spec(String fileName) {
    return OpenApiSpec.fromPath(Path.of(fileName));
  }

  private static Map<String, String> files(String... pathAndContent) {
    final Map<String, String> files = new HashMap<>();
    for (int i = 0; i < pathAndContent.length; i += 2) {
      files.put(pathAndContent[i], pathAndContent[i + 1]);
    }
    return files;
  }

  private static SwaggerSpecificationParser parserWithFiles(Map<String, String> files) {
    final SpecificationReader reader =
        (mainDirectory, specification) -> {
          final Path path = specification.asPathWithMainDirectory(mainDirectory);
          final String content = files.get(path.getFileName().toString());
          if (content == null) {
            throw new IllegalStateException("Unknown specification: " + path);
          }
          return content;
        };
    return new SwaggerSpecificationParser(reader, "Dto");
  }
}
