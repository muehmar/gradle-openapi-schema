package com.github.muehmar.gradle.openapi.generator.mapper.reader;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.specification.MainDirectory;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import java.io.IOException;
import java.util.Objects;
import org.gradle.api.GradleException;

public class SwaggerSpecificationParser implements SpecificationParser {
  private final SpecificationReader specReader;
  private final String pojoSuffix;

  public SwaggerSpecificationParser(SpecificationReader specReader, String pojoSuffix) {
    this.specReader = specReader;
    this.pojoSuffix = pojoSuffix;
  }

  @Override
  public PList<PojoSchema> readSchemas(MainDirectory mainDirectory, OpenApiSpec inputSpec) {
    try {
      final String specString = specReader.read(mainDirectory, inputSpec);
      final OpenAPI openAPI = parseSpec(specString);
      return convertToPojoSchemas(openAPI);
    } catch (IOException e) {
      throw new GradleException("Error while generating the schema classes", e);
    }
  }

  private PList<PojoSchema> convertToPojoSchemas(OpenAPI openAPI) {
    return PList.fromIter(openAPI.getComponents().getSchemas().entrySet())
        .filter(Objects::nonNull)
        .map(
            entry ->
                new PojoSchema(
                    PojoName.ofNameAndSuffix(entry.getKey(), pojoSuffix),
                    (Schema<?>) entry.getValue()));
  }

  private OpenAPI parseSpec(String inputSpec) throws IOException {
    final SwaggerParseResult swaggerParseResult = new OpenAPIV3Parser().readContents(inputSpec);

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
