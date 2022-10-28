package com.github.muehmar.gradle.openapi.generator.mapper.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Parameter;
import com.github.muehmar.gradle.openapi.generator.model.ParsedSpecification;
import com.github.muehmar.gradle.openapi.generator.model.specification.MainDirectory;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import io.swagger.v3.oas.models.media.IntegerSchema;
import java.util.Comparator;
import org.junit.jupiter.api.Test;

class SwaggerSpecificationParserTest {
  private final SwaggerSpecificationParser parser =
      new SwaggerSpecificationParser(new ResourceSpecificationReader(), "Dto");

  @Test
  void parse_when_parametersSpec_then_correctParsed() {
    final MainDirectory mainDirectory = MainDirectory.fromString("/specifications/parameters");
    final OpenApiSpec inputSpec = OpenApiSpec.fromString("parameters.yml");

    final ParsedSpecification parsedSpecification = parser.parse(mainDirectory, inputSpec);

    final PList<Parameter> parameters =
        parsedSpecification.getParameters().sort(Comparator.comparing(Parameter::getName));

    assertEquals(2, parameters.size());
    final Parameter limitParam = parameters.apply(0);
    final Parameter offsetParam = parameters.apply(1);

    assertEquals("limitParam", limitParam.getName());
    assertTrue(limitParam.getSchema() instanceof IntegerSchema);
    assertEquals("offsetParam", offsetParam.getName());
    assertTrue(offsetParam.getSchema() instanceof IntegerSchema);
  }
}
