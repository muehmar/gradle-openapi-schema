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
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class GenerateSchemasTask extends DefaultTask {

  @Inject
  public GenerateSchemasTask(Project project, OpenApiSchemaGeneratorExtension config) {
    // Use an inner class instead of a lambda to support incremental build properly
    doLast(
        new Action<Task>() {
          @Override
          public void execute(Task task) {
            try {
              runTask(project, config, task);
            } catch (IOException e) {
              throw new GradleException("Error while generating the schema classes", e);
            }
          }
        });
  }

  private void runTask(Project project, OpenApiSchemaGeneratorExtension config, Task task)
      throws IOException {
    final OpenAPI openAPI = parseSpec(config.getInputSpec());

    final String outputDir = config.getOutputDir(project);

    final JavaPojoGenerator javaPojoGenerator =
        new JavaPojoGenerator(task.getProject(), config, openAPI, WriterImpl::new);
    javaPojoGenerator.generate(outputDir);
  }

  private OpenAPI parseSpec(String inputSpec) throws IOException {
    final String openapiString =
        new String(Files.readAllBytes(Paths.get(inputSpec)), StandardCharsets.UTF_8);

    final SwaggerParseResult swaggerParseResult = new OpenAPIV3Parser().readContents(openapiString);

    return swaggerParseResult.getOpenAPI();
  }
}
