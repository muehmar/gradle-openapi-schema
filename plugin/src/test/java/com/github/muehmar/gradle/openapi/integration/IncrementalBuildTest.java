package com.github.muehmar.gradle.openapi.integration;

import static com.github.muehmar.gradle.openapi.Util.writeFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.muehmar.gradle.openapi.Resources;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Test;

class IncrementalBuildTest {
  private static final String MODULE_NAME = "incrementalbuild";

  @Test
  void runTwice_when_noModification_then_secondTimeTaskIsUpToDate() throws IOException {
    final TemporaryProject project = TemporaryProject.createStandard(MODULE_NAME);

    final BuildResult result =
        GradleRunner.create()
            .withProjectDir(project.getProjectDir().getRoot())
            .withPluginClasspath()
            .withArguments("generateApiSchemas")
            .build();

    final BuildTask generateTask = result.task(":generateApiSchemas");
    assertNotNull(generateTask);
    assertEquals(TaskOutcome.SUCCESS, generateTask.getOutcome());

    final BuildResult secondResult =
        GradleRunner.create()
            .withProjectDir(project.getProjectDir().getRoot())
            .withPluginClasspath()
            .withArguments("generateApiSchemas")
            .build();

    final BuildTask generateTaskSecondTime = secondResult.task(":generateApiSchemas");
    assertNotNull(generateTaskSecondTime);
    assertEquals(TaskOutcome.UP_TO_DATE, generateTaskSecondTime.getOutcome());
  }

  @Test
  void runTwice_when_inputSpecChanged_then_secondTimeTaskWasExecutedAgain() throws IOException {
    final TemporaryProject project = TemporaryProject.createStandard(MODULE_NAME);

    final BuildResult result =
        GradleRunner.create()
            .withProjectDir(project.getProjectDir().getRoot())
            .withPluginClasspath()
            .withArguments("generateApiSchemas")
            .build();

    final BuildTask generateTask = result.task(":generateApiSchemas");
    assertNotNull(generateTask);
    assertEquals(TaskOutcome.SUCCESS, generateTask.getOutcome());

    writeFile(
        project.getOpenApiSpec(),
        Resources.readString(
            TemporaryProject.INTEGRATION_RESOURCE_BASE_PATH
                + "/"
                + MODULE_NAME
                + "/changedOpenapi.yml"));

    final BuildResult secondResult =
        GradleRunner.create()
            .withProjectDir(project.getProjectDir().getRoot())
            .withPluginClasspath()
            .withArguments("generateApiSchemas")
            .build();

    final BuildTask generateTaskSecondTime = secondResult.task(":generateApiSchemas");
    assertNotNull(generateTaskSecondTime);
    assertEquals(TaskOutcome.SUCCESS, generateTaskSecondTime.getOutcome());
  }

  @Test
  void runTwice_when_inputPluginConfigChanged_then_secondTimeTaskWasExecutedAgain()
      throws IOException {
    final TemporaryProject project = TemporaryProject.createStandard(MODULE_NAME);

    final BuildResult result =
        GradleRunner.create()
            .withProjectDir(project.getProjectDir().getRoot())
            .withPluginClasspath()
            .withArguments("generateApiSchemas")
            .build();

    final BuildTask generateTask = result.task(":generateApiSchemas");
    assertNotNull(generateTask);
    assertEquals(TaskOutcome.SUCCESS, generateTask.getOutcome());

    writeFile(
        project.getBuildFile(),
        Resources.readString(TemporaryProject.INTEGRATION_RESOURCE_BASE_PATH + "/build.gradle")
            .replace("suffix = \"Dto\"", "suffix = \"\""));

    final BuildResult secondResult =
        GradleRunner.create()
            .withProjectDir(project.getProjectDir().getRoot())
            .withPluginClasspath()
            .withArguments("generateApiSchemas")
            .build();

    final BuildTask generateTaskSecondTime = secondResult.task(":generateApiSchemas");
    assertNotNull(generateTaskSecondTime);
    assertEquals(TaskOutcome.SUCCESS, generateTaskSecondTime.getOutcome());
  }

  @Test
  void runTwice_when_outputChanged_then_secondTimeTaskWasExecutedAgain() throws IOException {
    final TemporaryProject project = TemporaryProject.createStandard(MODULE_NAME);

    final BuildResult result =
        GradleRunner.create()
            .withProjectDir(project.getProjectDir().getRoot())
            .withPluginClasspath()
            .withArguments("generateApiSchemas")
            .build();

    final BuildTask generateTask = result.task(":generateApiSchemas");
    assertNotNull(generateTask);
    assertEquals(TaskOutcome.SUCCESS, generateTask.getOutcome());

    Files.delete(
        Paths.get(
            project.getProjectDir().getRoot().getPath()
                + "/build/generated/openapi/openapischema/api/model/UserDto.java"));

    final BuildResult secondResult =
        GradleRunner.create()
            .withProjectDir(project.getProjectDir().getRoot())
            .withPluginClasspath()
            .withArguments("generateApiSchemas")
            .build();

    final BuildTask generateTaskSecondTime = secondResult.task(":generateApiSchemas");
    assertNotNull(generateTaskSecondTime);
    assertEquals(TaskOutcome.SUCCESS, generateTaskSecondTime.getOutcome());
  }
}
