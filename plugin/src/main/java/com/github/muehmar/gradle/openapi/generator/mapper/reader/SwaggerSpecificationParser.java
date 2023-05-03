package com.github.muehmar.gradle.openapi.generator.mapper.reader;

import static java.util.Objects.nonNull;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.ParameterSchema;
import com.github.muehmar.gradle.openapi.generator.model.ParsedSpecification;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.schema.OpenApiSchema;
import com.github.muehmar.gradle.openapi.generator.model.specification.MainDirectory;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.gradle.api.GradleException;

public class SwaggerSpecificationParser implements SpecificationParser {
  private final SpecificationReader specReader;
  private final String pojoSuffix;

  public SwaggerSpecificationParser(SpecificationReader specReader, String pojoSuffix) {
    this.specReader = specReader;
    this.pojoSuffix = pojoSuffix;
  }

  @Override
  public ParsedSpecification parse(MainDirectory mainDirectory, OpenApiSpec inputSpec) {
    final String specString = specReader.read(mainDirectory, inputSpec);
    final OpenAPI openAPI = parseSpec(specString);
    return parse(openAPI);
  }

  private ParsedSpecification parse(OpenAPI openAPI) {
    final PList<PojoSchema> pojoSchemas = parsePojoSchemas(openAPI);
    final PList<ParameterSchema> parameters = parseParameters(openAPI);
    return new ParsedSpecification(pojoSchemas, parameters);
  }

  private PList<PojoSchema> parsePojoSchemas(OpenAPI openAPI) {
    return PList.fromOptional(Optional.ofNullable(openAPI.getComponents().getSchemas()))
        .flatMap(Map::entrySet)
        .filter(Objects::nonNull)
        .map(
            entry ->
                new PojoSchema(
                    PojoName.ofNameAndSuffix(entry.getKey(), pojoSuffix),
                    OpenApiSchema.wrapSchema(entry.getValue())));
  }

  private PList<ParameterSchema> parseParameters(OpenAPI openAPI) {
    return PList.fromOptional(Optional.ofNullable(openAPI.getComponents().getParameters()))
        .flatMap(Map::entrySet)
        .filter(Objects::nonNull)
        .filter(entry -> nonNull(entry.getValue().getSchema()))
        .map(
            entry ->
                new ParameterSchema(
                    Name.ofString(entry.getKey()),
                    OpenApiSchema.wrapSchema(entry.getValue().getSchema())));
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
