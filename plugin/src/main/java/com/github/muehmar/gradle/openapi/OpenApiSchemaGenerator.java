package com.github.muehmar.gradle.openapi;

import com.github.muehmar.gradle.openapi.dsl.OpenApiSchemaExtension;
import com.github.muehmar.gradle.openapi.dsl.SingleSchemaExtension;
import com.github.muehmar.gradle.openapi.task.GenerateSchemasTask;
import java.io.File;
import java.util.Optional;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;

public class OpenApiSchemaGenerator implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    final OpenApiSchemaExtension openApiGenerator =
        project.getExtensions().create("openApiGenerator", OpenApiSchemaExtension.class);

    project.afterEvaluate(
        p ->
            openApiGenerator
                .getSchemaExtensions()
                .forEach(
                    singleSchemaExtension -> setupGenerateModelTask(p, singleSchemaExtension)));
  }

  private void setupGenerateModelTask(Project project, SingleSchemaExtension extension) {
    final TaskProvider<GenerateSchemasTask> generateModelTask = createTask(project, extension);
    attachToCompileJavaTask(project, extension, generateModelTask);
    setupSourceSet(project, extension);
  }

  private void setupSourceSet(Project project, SingleSchemaExtension extension) {
    final SourceSetContainer sourceSets =
        Optional.ofNullable(project.getExtensions().findByType(JavaPluginExtension.class))
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "Unable to obtain the Java SourceSets, most likely the Java Plugin is not applied to project "
                            + project.getName()))
            .getSourceSets();

    sourceSets.forEach(
        sourceSet -> {
          if (sourceSet.getName().equals(extension.getSourceSet())) {
            final String sourceDir = extension.getOutputDir(project);
            new File(sourceDir).mkdirs();
            sourceSet.getJava().srcDir(sourceDir);
          }
        });
  }

  private void attachToCompileJavaTask(
      Project project,
      SingleSchemaExtension extension,
      TaskProvider<GenerateSchemasTask> generateModelTask) {
    final String compileJavaTaskName =
        String.format("compile%sJava", capitalize(extension.getSourceSet()))
            .replace("compileMainJava", "compileJava");

    Optional.ofNullable(project.getTasks().findByName(compileJavaTaskName))
        .ifPresent(javaTask -> javaTask.dependsOn(generateModelTask));
  }

  private TaskProvider<GenerateSchemasTask> createTask(
      Project project, SingleSchemaExtension extension) {
    final String generateModelTaskName =
        String.format("generate%sModel", capitalize(extension.getName()));

    return project
        .getTasks()
        .register(generateModelTaskName, GenerateSchemasTask.class, project, extension);
  }

  private static String capitalize(String input) {
    return input.substring(0, 1).toUpperCase() + input.substring(1);
  }
}
