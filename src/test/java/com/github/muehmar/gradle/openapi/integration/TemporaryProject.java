package com.github.muehmar.gradle.openapi.integration;

import static com.github.muehmar.gradle.openapi.Util.writeFile;

import com.github.muehmar.gradle.openapi.Resources;
import java.io.File;
import java.io.IOException;
import org.gradle.internal.impldep.org.junit.rules.TemporaryFolder;

public class TemporaryProject {
  private final TemporaryFolder projectDir;
  private final File settingsFile;
  private final File buildFile;
  private final File openApiSpec;

  public TemporaryProject(
      TemporaryFolder projectDir, File settingsFile, File buildFile, File openApiSpec) {
    this.projectDir = projectDir;
    this.settingsFile = settingsFile;
    this.buildFile = buildFile;
    this.openApiSpec = openApiSpec;
  }

  public static TemporaryProject createEmpty() throws IOException {
    final TemporaryFolder projectDir = new TemporaryFolder(new File("build"));
    projectDir.create();
    final File settingsFile = projectDir.newFile("settings.gradle");
    final File buildFile = projectDir.newFile("build.gradle");
    final File openApiSpec = projectDir.newFile("openapi.yml");
    return new TemporaryProject(projectDir, settingsFile, buildFile, openApiSpec);
  }

  public static TemporaryProject createStandard(String resourcePath) throws IOException {
    final TemporaryProject project = createEmpty();

    writeFile(project.getSettingsFile(), "rootProject.name = 'openapi-schema'");
    writeFile(project.getBuildFile(), Resources.readString(resourcePath + "/build.gradle"));
    writeFile(project.getOpenApiSpec(), Resources.readString(resourcePath + "/openapi.yml"));
    return project;
  }

  public TemporaryFolder getProjectDir() {
    return projectDir;
  }

  public File getSettingsFile() {
    return settingsFile;
  }

  public File getBuildFile() {
    return buildFile;
  }

  public File getOpenApiSpec() {
    return openApiSpec;
  }
}
