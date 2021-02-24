package com.github.muehmar.gradle.openapi.task;

import com.github.muehmar.gradle.openapi.OpenApiSchemaGeneratorExtension;
import com.github.muehmar.gradle.openapi.generator.JavaPojoGenerator;
import com.github.muehmar.gradle.openapi.writer.WriterImpl;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.inject.Inject;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;

public class GenerateSchemasTask extends DefaultTask {

  @Inject
  public GenerateSchemasTask(OpenApiSchemaGeneratorExtension config, Project project) {
    doLast(
        task -> {
          try {
            final String inputSpec =
                config
                    .getInputSpec()
                    .orElseThrow(
                        () ->
                            new InvalidUserDataException(
                                "Could not generate schema, no input spec defined: Declare a correct path to a valid openapi spec."));

            final OpenAPI openAPI = parseSpec(inputSpec);

            final String outputDir = config.getOutputDir(project);

            final JavaPojoGenerator javaPojoGenerator =
                new JavaPojoGenerator(task.getProject(), config, openAPI, WriterImpl::new);
            javaPojoGenerator.generate(outputDir);

          } catch (IOException e) {
            throw new GradleException("Error while generating the schema classes", e);
          }
        });
  }

  private OpenAPI parseSpec(String inputSpec) throws IOException {
    final String openapiString =
        new String(Files.readAllBytes(Paths.get(inputSpec)), StandardCharsets.UTF_8);

    final SwaggerParseResult swaggerParseResult = new OpenAPIV3Parser().readContents(openapiString);

    return swaggerParseResult.getOpenAPI();
  }
}
