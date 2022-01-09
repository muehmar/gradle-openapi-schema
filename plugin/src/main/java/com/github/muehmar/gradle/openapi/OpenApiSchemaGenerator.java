package com.github.muehmar.gradle.openapi;

import com.github.muehmar.gradle.openapi.task.GenerateSchemasTask;
import java.io.File;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;

public class OpenApiSchemaGenerator implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    final OpenApiSchemaGeneratorExtension config =
        project.getExtensions().create("generateApiSchemas", OpenApiSchemaGeneratorExtension.class);

    final TaskProvider<GenerateSchemasTask> createTask =
        project
            .getTasks()
            .register("generateApiSchemas", GenerateSchemasTask.class, project, config);

    createTask.configure(
        task -> {
          task.getInputs().file(config.getInputSpec());
          task.getOutputs().dir(config.getOutputDir(project));
          task.setGroup("openapi schema generator");
        });

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
