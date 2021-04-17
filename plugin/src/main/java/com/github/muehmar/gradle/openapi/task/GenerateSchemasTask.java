package com.github.muehmar.gradle.openapi.task;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.OpenApiSchemaGeneratorExtension;
import com.github.muehmar.gradle.openapi.generator.GeneratorFactory;
import com.github.muehmar.gradle.openapi.generator.OpenApiGenerator;
import com.github.muehmar.gradle.openapi.generator.data.Pojo;
import com.github.muehmar.gradle.openapi.generator.settings.Language;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;

public class GenerateSchemasTask extends DefaultTask {

  private final Provider<String> inputSpec;
  private final Provider<String> outputDir;
  private final Provider<PojoSettings> pojoSettings;
  private final Provider<String> sourceSet;

  @Inject
  @SuppressWarnings("java:S1604")
  public GenerateSchemasTask(Project project, OpenApiSchemaGeneratorExtension config) {
    sourceSet = project.getProviders().provider(config::getSourceSet);
    inputSpec = project.getProviders().provider(config::getInputSpec);
    outputDir = project.getProviders().provider(() -> config.getOutputDir(project));
    pojoSettings =
        project
            .getProviders()
            .provider(() -> PojoSettings.fromOpenApiSchemaGeneratorExtension(config, project));

    // Use an inner class instead of a lambda to support incremental build properly
    doLast(
        new Action<Task>() {
          @Override
          public void execute(Task task) {
            try {
              runTask();
            } catch (IOException e) {
              throw new GradleException("Error while generating the schema classes", e);
            }
          }
        });
  }

  private void runTask() throws IOException {
    final OpenAPI openAPI = parseSpec(inputSpec.get());

    final OpenApiGenerator openApiGenerator =
        GeneratorFactory.create(Language.JAVA, outputDir.get());

    final PList<Pojo> pojos =
        PList.fromIter(openAPI.getComponents().getSchemas().entrySet())
            .filter(Objects::nonNull)
            .flatMap(
                entry ->
                    openApiGenerator
                        .getMapper()
                        .fromSchema(entry.getKey(), entry.getValue(), pojoSettings.get()));

    pojos.forEach(pojo -> openApiGenerator.getGenerator().generatePojo(pojo, pojoSettings.get()));
  }

  private OpenAPI parseSpec(String inputSpec) throws IOException {
    final String openapiString =
        new String(Files.readAllBytes(Paths.get(inputSpec)), StandardCharsets.UTF_8);

    final SwaggerParseResult swaggerParseResult = new OpenAPIV3Parser().readContents(openapiString);

    return swaggerParseResult.getOpenAPI();
  }

  @Input
  public Provider<String> getInputSpec() {
    return inputSpec;
  }

  @Input
  public Provider<String> getOutputDir() {
    return outputDir;
  }

  @Input
  public Provider<PojoSettings> getPojoSettings() {
    return pojoSettings;
  }

  @Input
  public Provider<String> getSourceSet() {
    return sourceSet;
  }
}
