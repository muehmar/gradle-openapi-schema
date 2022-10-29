package com.github.muehmar.gradle.openapi.task;

import com.github.muehmar.gradle.openapi.dsl.SingleSchemaExtension;
import com.github.muehmar.gradle.openapi.generator.GeneratorFactory;
import com.github.muehmar.gradle.openapi.generator.Generators;
import com.github.muehmar.gradle.openapi.generator.java.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.TristateGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.jackson.JacksonNullContainerGenerator;
import com.github.muehmar.gradle.openapi.generator.mapper.MapResult;
import com.github.muehmar.gradle.openapi.generator.mapper.PojoMapperFactory;
import com.github.muehmar.gradle.openapi.generator.mapper.SpecificationMapper;
import com.github.muehmar.gradle.openapi.generator.model.specification.MainDirectory;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import com.github.muehmar.gradle.openapi.generator.settings.Language;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.util.Suppliers;
import com.github.muehmar.gradle.openapi.writer.FileWriter;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Supplier;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;

public class GenerateSchemasTask extends DefaultTask {

  private final String inputSpec;
  private final MainDirectory mainDirectory;
  private final Provider<FileCollection> usedSpecifications;
  private final Provider<String> outputDir;
  private final Provider<PojoSettings> pojoSettings;
  private final Provider<String> sourceSet;
  private final Supplier<MapResult> cachedMapping;

  @Inject
  @SuppressWarnings("java:S1604")
  public GenerateSchemasTask(Project project, SingleSchemaExtension extension) {
    setGroup("openapi schema generator");

    inputSpec = extension.getInputSpec();
    mainDirectory = deviateMainDirectory(inputSpec);

    cachedMapping = Suppliers.cached(this::executeMapping);

    sourceSet = project.getProviders().provider(extension::getSourceSet);
    usedSpecifications = usedSpecificationsProvider(project, extension);
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
    final MapResult mapResult = cachedMapping.get();
    final Generators generators = GeneratorFactory.create(Language.JAVA, outputDir.get());

    mapResult
        .getPojos()
        .forEach(pojo -> generators.getPojoGenerator().generatePojo(pojo, pojoSettings.get()));

    generators.getParametersGenerator().generate(mapResult.getParameters(), pojoSettings.get());

    writeOpenApiUtils();
  }

  private MapResult executeMapping() {
    final SpecificationMapper specificationMapper =
        PojoMapperFactory.create(pojoSettings.get().getSuffix());
    final Path specPath = Paths.get(inputSpec);
    final OpenApiSpec openApiSpec = OpenApiSpec.fromPath(specPath.getFileName());
    return specificationMapper.map(
        mainDirectory, openApiSpec, pojoSettings.get().getExcludedSchemas());
  }

  private Provider<FileCollection> usedSpecificationsProvider(
      Project project, SingleSchemaExtension extension) {
    return project
        .getProviders()
        .provider(
            () -> {
              if (extension.getResolveInputSpecs()) {
                final Object[] specs =
                    cachedMapping
                        .get()
                        .getUsedSpecs()
                        .map(spec -> spec.asPathWithMainDirectory(mainDirectory).toString())
                        .toArray(String.class);
                return project.files(specs);
              } else {
                return project.files(inputSpec);
              }
            });
  }

  private static MainDirectory deviateMainDirectory(String inputSpec) {
    final Path specPath = Paths.get(inputSpec);
    return MainDirectory.fromPath(Optional.ofNullable(specPath.getParent()).orElse(Paths.get("")));
  }

  private void writeOpenApiUtils() {
    final Generator<Void, Void> tristateGen = TristateGenerator.tristateClass();
    final FileWriter tristateWriter = new FileWriter(outputDir.get());
    tristateWriter.println(tristateGen.generate(null, null, Writer.createDefault()).asString());
    tristateWriter.close(OpenApiUtilRefs.TRISTATE.replace(".", File.separator) + ".java");

    final Generator<Void, Void> jacksonContainerGen =
        JacksonNullContainerGenerator.containerClass();
    final FileWriter jacksonContainerWriter = new FileWriter(outputDir.get());
    jacksonContainerWriter.println(
        jacksonContainerGen.generate(null, null, Writer.createDefault()).asString());
    jacksonContainerWriter.close(
        OpenApiUtilRefs.JACKSON_NULL_CONTAINER.replace(".", File.separator) + ".java");
  }

  @InputFiles
  public Provider<FileCollection> getUsedSpecifications() {
    return usedSpecifications;
  }

  @OutputDirectory
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
