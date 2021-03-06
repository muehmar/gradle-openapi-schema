package com.github.muehmar.gradle.openapi.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Test;

class CompleteSpecTest {
  private static final String MODULE_NAME = "completespec";

  @Test
  void run_when_completeSpec_then_taskCompletesSuccessfully() throws IOException {
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
  }
}
