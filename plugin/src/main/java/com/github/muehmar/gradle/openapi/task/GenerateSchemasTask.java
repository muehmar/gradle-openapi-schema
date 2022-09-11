package com.github.muehmar.gradle.openapi.task;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.dsl.SingleSchemaExtension;
import com.github.muehmar.gradle.openapi.generator.GeneratorFactory;
import com.github.muehmar.gradle.openapi.generator.PojoGenerator;
import com.github.muehmar.gradle.openapi.generator.PojoMapper;
import com.github.muehmar.gradle.openapi.generator.PojoMapperFactory;
import com.github.muehmar.gradle.openapi.generator.java.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.TristateGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.jackson.JacksonNullContainerGenerator;
import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.settings.Language;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.writer.FileWriter;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
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
  public GenerateSchemasTask(Project project, SingleSchemaExtension extension) {
    sourceSet = project.getProviders().provider(extension::getSourceSet);
    inputSpec = project.getProviders().provider(extension::getInputSpec);
    outputDir = project.getProviders().provider(() -> extension.getOutputDir(project));
    pojoSettings = project.getProviders().provider(() -> extension.toPojoSettings(project));

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

    final PList<OpenApiPojo> openApiPojos =
        PList.fromIter(openAPI.getComponents().getSchemas().entrySet())
            .filter(Objects::nonNull)
            .map(
                entry ->
                    new OpenApiPojo(
                        PojoName.ofNameAndSuffix(entry.getKey(), pojoSettings.get().getSuffix()),
                        (Schema<?>) entry.getValue()));

    final PojoMapper pojoMapper = PojoMapperFactory.create();
    final PList<Pojo> pojos = pojoMapper.fromSchemas(openApiPojos);
    final PojoGenerator generator =
        GeneratorFactory.createGenerator(Language.JAVA, outputDir.get());
    pojos.forEach(pojo -> generator.generatePojo(pojo, pojoSettings.get()));

    writeOpenApiUtils();
  }

  private void writeOpenApiUtils() {
    final Generator<Void, Void> tristateGen = TristateGenerator.tristateClass();
    final FileWriter tristateWriter = new FileWriter(outputDir.get());
    tristateWriter.println(tristateGen.generate(null, null, Writer.createDefault()).asString());
    tristateWriter.close(OpenApiUtilRefs.TRISTATE.replace(".", "/") + ".java");

    final Generator<Void, Void> jacksonContainerGen =
        JacksonNullContainerGenerator.containerClass();
    final FileWriter jacksonContainerWriter = new FileWriter(outputDir.get());
    jacksonContainerWriter.println(
        jacksonContainerGen.generate(null, null, Writer.createDefault()).asString());
    jacksonContainerWriter.close(
        OpenApiUtilRefs.JACKSON_NULL_CONTAINER.replace(".", "/") + ".java");
  }

  private OpenAPI parseSpec(String inputSpec) throws IOException {
    final String openapiString =
        new String(Files.readAllBytes(Paths.get(inputSpec)), StandardCharsets.UTF_8);

    final SwaggerParseResult swaggerParseResult = new OpenAPIV3Parser().readContents(openapiString);

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
