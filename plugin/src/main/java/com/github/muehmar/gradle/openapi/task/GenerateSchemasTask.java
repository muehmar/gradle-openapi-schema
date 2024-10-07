package com.github.muehmar.gradle.openapi.task;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.dsl.SingleSchemaExtension;
import com.github.muehmar.gradle.openapi.dsl.WarningsConfig;
import com.github.muehmar.gradle.openapi.generator.GeneratorFactory;
import com.github.muehmar.gradle.openapi.generator.Generators;
import com.github.muehmar.gradle.openapi.generator.mapper.MapResult;
import com.github.muehmar.gradle.openapi.generator.mapper.PojoMapperFactory;
import com.github.muehmar.gradle.openapi.generator.mapper.SpecificationMapper;
import com.github.muehmar.gradle.openapi.generator.model.Parameter;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.specification.MainDirectory;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import com.github.muehmar.gradle.openapi.generator.settings.Language;
import com.github.muehmar.gradle.openapi.generator.settings.PojoNameMapping;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.util.Suppliers;
import com.github.muehmar.gradle.openapi.warnings.WarningsContext;
import com.github.muehmar.gradle.openapi.warnings.WarningsHandler;
import com.github.muehmar.gradle.openapi.writer.BaseDirFileWriter;
import com.github.muehmar.gradle.openapi.writer.FileWriter;
import com.github.muehmar.gradle.openapi.writer.GeneratedFile;
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
  private final Provider<WarningsConfig> warningsConfig;
  private final Provider<String> sourceSet;
  private final Supplier<MapResult> cachedMapping;

  @Inject
  @SuppressWarnings("java:S1604")
  public GenerateSchemasTask(Project project, SingleSchemaExtension extension, String taskName) {
    setGroup("openapi schema generator");

    inputSpec = extension.getInputSpec();
    mainDirectory = deviateMainDirectory(inputSpec);

    cachedMapping = Suppliers.cached(this::executeMapping);

    sourceSet = project.getProviders().provider(extension::getSourceSet);
    usedSpecifications = usedSpecificationsProvider(project, extension);
    outputDir = project.getProviders().provider(() -> extension.getOutputDir(project));
    pojoSettings =
        project.getProviders().provider(() -> extension.toPojoSettings(project, taskName));
    warningsConfig = project.getProviders().provider(extension::getWarnings);

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
    WarningsContext.resetWarningsForTask(pojoSettings.get().getTaskIdentifier());
    final MapResult mapResult = cachedMapping.get();
    final Generators generators = GeneratorFactory.create(Language.JAVA);

    mapResult.getPojos().flatMap(pojo -> createPojo(pojo, generators)).forEach(this::writeFile);

    mapResult
        .getParameters()
        .map(parameter -> createParameter(parameter, generators))
        .forEach(this::writeFile);

    generators.getUtilsGenerator().generateUtils(pojoSettings.get()).forEach(this::writeFile);

    handleWarnings();
  }

  private void handleWarnings() {
    pojoSettings.get().validate();
    if (not(warningsConfig.get().getDisableWarnings())) {
      WarningsHandler.handleWarnings(
          pojoSettings.get().getTaskIdentifier(), warningsConfig.get().getFailingWarningTypes());
    }
  }

  private NonEmptyList<GeneratedFile> createPojo(Pojo pojo, Generators generators) {
    return generators.getPojoGenerator().generatePojo(pojo, pojoSettings.get());
  }

  private GeneratedFile createParameter(Parameter parameter, Generators generators) {
    return generators.getParametersGenerator().generate(parameter, pojoSettings.get());
  }

  private void writeFile(GeneratedFile file) {
    final FileWriter fileWriter = new BaseDirFileWriter(outputDir.get());
    fileWriter.writeFile(file);
  }

  private MapResult executeMapping() {
    final SpecificationMapper specificationMapper =
        PojoMapperFactory.create(pojoSettings.get().getSuffix());
    final Path specPath = Paths.get(inputSpec);
    final OpenApiSpec openApiSpec = OpenApiSpec.fromPath(specPath.getFileName());
    final PojoNameMapping pojoNameMapping = pojoSettings.get().pojoNameMapping();
    final MapResult mapResult =
        specificationMapper.map(
            mainDirectory, openApiSpec, pojoSettings.get().getExcludedSchemas());
    return mapResult.mapPojos(pojo -> pojo.applyMapping(pojoNameMapping));
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
