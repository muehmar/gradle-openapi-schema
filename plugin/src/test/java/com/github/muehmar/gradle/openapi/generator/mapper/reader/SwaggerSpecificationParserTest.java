package com.github.muehmar.gradle.openapi.generator.mapper.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.ParameterSchema;
import com.github.muehmar.gradle.openapi.generator.model.ParsedSpecification;
import com.github.muehmar.gradle.openapi.generator.model.schema.IntegerSchema;
import com.github.muehmar.gradle.openapi.generator.model.specification.MainDirectory;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import java.nio.file.Paths;
import java.util.Comparator;
import org.junit.jupiter.api.Test;

class SwaggerSpecificationParserTest {
  private final SwaggerSpecificationParser parser =
      new SwaggerSpecificationParser(new ResourceSpecificationReader(), "Dto");

  @Test
  void parse_when_parametersSpec_then_correctParsed() {
    final MainDirectory mainDirectory = MainDirectory.fromString("/specifications/parameters");
    final OpenApiSpec inputSpec = OpenApiSpec.fromPath(Paths.get("parameters.yml"));

    final ParsedSpecification parsedSpecification = parser.parse(mainDirectory, inputSpec);

    final PList<ParameterSchema> parameters =
        parsedSpecification
            .getParameters()
            .sort(Comparator.comparing(parameterSchema -> parameterSchema.getName().asString()));

    assertEquals(2, parameters.size());
    final ParameterSchema limitParam = parameters.apply(0);
    final ParameterSchema offsetParam = parameters.apply(1);

    assertEquals("limitParam", limitParam.getName().asString());
    assertInstanceOf(IntegerSchema.class, limitParam.getSchema());
    assertEquals("offsetParam", offsetParam.getName().asString());
    assertInstanceOf(IntegerSchema.class, offsetParam.getSchema());
  }
}
