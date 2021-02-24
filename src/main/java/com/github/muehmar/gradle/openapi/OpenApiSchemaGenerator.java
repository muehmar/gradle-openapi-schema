package com.github.muehmar.gradle.openapi;

import com.github.muehmar.gradle.openapi.task.GenerateSchemasTask;
import java.io.File;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;

public class OpenApiSchemaGenerator implements Plugin<Project> {
  @Override
  public void apply(Project project) {

    final NamedDomainObjectContainer<OpenApiSchemaGeneratorExtension.ClassMapping> classMappings =
        project.container(OpenApiSchemaGeneratorExtension.ClassMapping.class);

    final NamedDomainObjectContainer<OpenApiSchemaGeneratorExtension.FormatTypeMapping>
        formatTypeMappings =
            project.container(OpenApiSchemaGeneratorExtension.FormatTypeMapping.class);

    final OpenApiSchemaGeneratorExtension config =
        project
            .getExtensions()
            .create(
                "generateApiSchemas",
                OpenApiSchemaGeneratorExtension.class,
                classMappings,
                formatTypeMappings);

    final TaskProvider<GenerateSchemasTask> createTask =
        project
            .getTasks()
            .register("generateApiSchemas", GenerateSchemasTask.class, config, project);
    createTask.configure(task -> task.setGroup("openapi schema generator"));

    project.afterEvaluate(
        prj -> {
          final SourceSetContainer sourceSets =
              prj.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets();

          sourceSets.forEach(
              sourceSet -> {
                if (sourceSet.getName().equals(config.getSourceSet())) {
                  final String sourceDir = config.getOutputDir(project);
                  new File(sourceDir).mkdirs();
                  sourceSet.getJava().srcDir(sourceDir);
                }
              });
        });
  }
}
