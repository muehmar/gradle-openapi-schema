package com.github.muehmar.gradle.openapi.warnings;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.PropertyInfoNames;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.java.model.validation.ConstraintType;
import com.github.muehmar.gradle.openapi.task.TaskIdentifier;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class WarningsContextTest {
  @Test
  void getWarnings_when_addedWithMultipleTasksAndMultipleTimes_correctWarningsPerTaskAndUnique() {
    final Warning warning1 =
        Warning.unsupportedValidation(
            PropertyInfoNames.invoiceNumber(), JavaTypes.stringType(), ConstraintType.MIN);
    final Warning warning2 =
        Warning.unsupportedValidation(
            PropertyInfoNames.invoiceNumber(), JavaTypes.integerType(), ConstraintType.PATTERN);
    final Warning warning3 =
        Warning.unsupportedValidation(
            PropertyInfoNames.invoiceNumber(), JavaTypes.integerType(), ConstraintType.EMAIL);

    final TaskIdentifier taskIdentifier1 = TaskIdentifier.fromString(UUID.randomUUID().toString());
    final TaskIdentifier taskIdentifier2 = TaskIdentifier.fromString(UUID.randomUUID().toString());

    WarningsContext.addWarningForTask(taskIdentifier1, warning1);
    WarningsContext.addWarningForTask(taskIdentifier1, warning2);
    WarningsContext.addWarningForTask(taskIdentifier1, warning2);
    WarningsContext.addWarningForTask(taskIdentifier2, warning3);
    WarningsContext.addWarningForTask(taskIdentifier2, warning3);

    assertEquals(
        PList.of(warning1, warning2).toHashSet(),
        WarningsContext.getWarnings(taskIdentifier1).getWarnings().toHashSet());
    assertEquals(
        PList.of(warning3).toHashSet(),
        WarningsContext.getWarnings(taskIdentifier2).getWarnings().toHashSet());
  }
}
