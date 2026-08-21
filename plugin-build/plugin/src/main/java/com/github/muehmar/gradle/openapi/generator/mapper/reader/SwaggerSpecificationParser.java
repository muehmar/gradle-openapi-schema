package com.github.muehmar.gradle.openapi.generator.mapper.reader;

import ch.bluecare.commons.data.PList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.muehmar.gradle.openapi.generator.model.ParsedSpecification;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.schema.OpenApiSchema;
import com.github.muehmar.gradle.openapi.generator.model.schema.SchemaWrapper;
import com.github.muehmar.gradle.openapi.generator.model.specification.MainDirectory;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.gradle.api.GradleException;

public class SwaggerSpecificationParser implements SpecificationParser {
  private static final String OPENAPI_FIELD = "openapi";
  private static final String COMPONENTS_FIELD = "components";
  private static final String SCHEMAS_FIELD = "schemas";

  private final SpecificationReader specReader;
  private final String pojoSuffix;

  private final AtomicReference<String> mainOpenApiVersion = new AtomicReference<>();

  public SwaggerSpecificationParser(SpecificationReader specReader, String pojoSuffix) {
    this.specReader = specReader;
    this.pojoSuffix = pojoSuffix;
  }

  @Override
  public ParsedSpecification parse(MainDirectory mainDirectory, OpenApiSpec inputSpec) {
    final String specString = specReader.read(mainDirectory, inputSpec);
    final String documentString = normalizeOpenApiString(mainDirectory, inputSpec, specString);
    final OpenAPI openAPI = parseSpec(documentString);
    return parse(inputSpec, openAPI);
  }

  private ParsedSpecification parse(OpenApiSpec spec, OpenAPI openAPI) {
    final PList<PojoSchema> pojoSchemas = parsePojoSchemas(spec, openAPI);
    return new ParsedSpecification(pojoSchemas);
  }

  private PList<PojoSchema> parsePojoSchemas(OpenApiSpec spec, OpenAPI openAPI) {
    return PList.fromOptional(Optional.ofNullable(openAPI.getComponents().getSchemas()))
        .flatMap(Map::entrySet)
        .filter(Objects::nonNull)
        .map(
            entry ->
                new PojoSchema(
                    ComponentName.fromSchemaStringAndSuffix(entry.getKey(), pojoSuffix),
                    OpenApiSchema.wrapSchema(new SchemaWrapper(spec, entry.getValue()))));
  }

  /**
   * Returns a full OpenAPI document unchanged. A schema-only file (schemas at the root or under
   * 'components/schemas', without an 'openapi' field) is wrapped into a synthetic document
   * inheriting the main specification's version.
   */
  private String normalizeOpenApiString(
      MainDirectory mainDirectory, OpenApiSpec spec, String specString) {
    return readObjectNode(specString)
        .map(root -> normalizeOpenApiString(mainDirectory, spec, specString, root))
        .orElse(specString);
  }

  private String normalizeOpenApiString(
      MainDirectory mainDirectory, OpenApiSpec spec, String specString, ObjectNode root) {
    final Optional<String> documentVersion =
        Optional.ofNullable(root.get(OPENAPI_FIELD))
            .filter(JsonNode::isTextual)
            .map(JsonNode::asText);

    return documentVersion
        .map(
            version -> {
              mainOpenApiVersion.compareAndSet(null, version);
              return specString;
            })
        .orElseGet(() -> wrapSchemasAsDocument(mainDirectory, spec, root));
  }

  private Optional<ObjectNode> readObjectNode(String specString) {
    final ObjectMapper mapper = specString.trim().startsWith("{") ? Json.mapper() : Yaml.mapper();
    try {
      final JsonNode node = mapper.readTree(specString);
      return node instanceof ObjectNode ? Optional.of((ObjectNode) node) : Optional.empty();
    } catch (JsonProcessingException e) {
      return Optional.empty();
    }
  }

  private String wrapSchemasAsDocument(
      MainDirectory mainDirectory, OpenApiSpec spec, ObjectNode root) {
    final String version =
        Optional.ofNullable(mainOpenApiVersion.get())
            .orElseThrow(
                () ->
                    new GradleException(
                        String.format(
                            "The specification '%s' does not contain the 'openapi' field. Only"
                                + " referenced specifications may omit it, the main specification"
                                + " must be a full OpenAPI document.",
                            spec.asPathWithMainDirectory(mainDirectory))));

    final ObjectMapper mapper = Yaml.mapper();
    final ObjectNode document = mapper.createObjectNode();
    document.put(OPENAPI_FIELD, version);
    final ObjectNode info = document.putObject("info");
    info.put("title", "Referenced schemas");
    info.put("version", "-");
    document.putObject("paths");
    if (root.has(COMPONENTS_FIELD)) {
      document.set(COMPONENTS_FIELD, root.get(COMPONENTS_FIELD));
    } else {
      document.putObject(COMPONENTS_FIELD).set(SCHEMAS_FIELD, root);
    }

    try {
      return mapper.writeValueAsString(document);
    } catch (JsonProcessingException e) {
      throw new GradleException(
          String.format(
              "Unable to process the referenced specification '%s'.",
              spec.asPathWithMainDirectory(mainDirectory)),
          e);
    }
  }

  private OpenAPI parseSpec(String inputSpec) {
    final OpenAPIV3Parser openAPIV3Parser = new OpenAPIV3Parser();
    final ParseOptions parseOptions = new ParseOptions();
    parseOptions.setResolve(false);
    parseOptions.setResolveFully(false);
    final SwaggerParseResult swaggerParseResult =
        openAPIV3Parser.readContents(inputSpec, null, parseOptions);

    final OpenAPI openAPI = swaggerParseResult.getOpenAPI();
    if (openAPI == null) {
      if (swaggerParseResult.getMessages() != null) {
        final String messages =
            PList.fromIter(swaggerParseResult.getMessages())
                .map(message -> String.format("%s", message))
                .mkString("\n\n");
        throw new GradleException(
            "Failed to parse the OpenAPI specification with the following messages: " + messages);
      }
      throw new GradleException("Unable to parse OpenAPI specification.");
    }
    return openAPI;
  }
}
