package com.github.muehmar.gradle.openapi.task;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.dsl.SingleSchemaExtension;
import com.github.muehmar.gradle.openapi.generator.GeneratorFactory;
import com.github.muehmar.gradle.openapi.generator.PojoGenerator;
import com.github.muehmar.gradle.openapi.generator.java.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.TristateGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.jackson.JacksonNullContainerGenerator;
import com.github.muehmar.gradle.openapi.generator.mapper.PojoMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.PojoMapperFactory;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.specification.MainDirectory;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import com.github.muehmar.gradle.openapi.generator.settings.Language;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.writer.FileWriter;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
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
            runTask();
          }
        });
  }

  private void runTask() {
    final PojoMapper pojoMapper = PojoMapperFactory.create(pojoSettings.get().getSuffix());
    final Path specPath = Paths.get(inputSpec.get());
    final OpenApiSpec openApiSpec = OpenApiSpec.fromPath(specPath.getFileName());
    final MainDirectory mainDirectory =
        MainDirectory.fromPath(Optional.ofNullable(specPath.getParent()).orElse(Paths.get("")));
    final PList<Pojo> pojos = pojoMapper.fromSpecification(mainDirectory, openApiSpec);
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
