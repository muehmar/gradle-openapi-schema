package com.github.muehmar.gradle.openapi.generator.java;

import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static org.junit.jupiter.api.Assertions.assertEquals;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import com.github.muehmar.gradle.openapi.writer.GeneratedFile;
import org.junit.jupiter.api.Test;

@SnapshotTest
class JavaPojoGeneratorTest {

  private Expect expect;

  @Test
  @SnapshotName("arrayPojo")
  void generatePojo_when_arrayPojo_then_correctPojoGenerated() {
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator();

    final NonEmptyList<GeneratedFile> generatedFiles =
        pojoGenerator.generatePojo(JavaPojos.arrayPojo(), defaultTestSettings());

    assertEquals(1, generatedFiles.size());

    expect.toMatchSnapshot(generatedFiles.head().getContent());
  }

  @Test
  @SnapshotName("objectPojo")
  void generatePojo_when_objectPojo_then_correctPojoGenerated() {
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator();

    final NonEmptyList<GeneratedFile> generatedFiles =
        pojoGenerator.generatePojo(sampleObjectPojo1(), defaultTestSettings());

    assertEquals(1, generatedFiles.size());

    expect.toMatchSnapshot(generatedFiles.head().getContent());
  }

  @Test
  @SnapshotName("enumPojo")
  void generatePojo_when_enumPojo_then_correctPojoGenerated() {
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator();

    final NonEmptyList<GeneratedFile> generatedFiles =
        pojoGenerator.generatePojo(JavaPojos.enumPojo(), defaultTestSettings());

    assertEquals(1, generatedFiles.size());

    expect.toMatchSnapshot(generatedFiles.head().getContent());
  }
}
