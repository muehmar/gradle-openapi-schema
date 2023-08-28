package com.github.muehmar.gradle.openapi.generator.java;

import static org.junit.jupiter.api.Assertions.assertEquals;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.IntellijDiffSnapshotTestExtension;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.*;
import com.github.muehmar.gradle.openapi.writer.GeneratedFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({SnapshotExtension.class, IntellijDiffSnapshotTestExtension.class})
class JavaPojoGeneratorTest {

  private Expect expect;

  @Test
  @SnapshotName("arrayPojo")
  void generatePojo_when_arrayPojo_then_correctPojoGenerated() {
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator();

    final NonEmptyList<GeneratedFile> generatedFiles =
        pojoGenerator.generatePojo(JavaPojos.arrayPojo(), TestPojoSettings.defaultSettings());

    assertEquals(1, generatedFiles.size());

    expect.toMatchSnapshot(generatedFiles.head().getContent());
  }

  @Test
  @SnapshotName("objectPojo")
  void generatePojo_when_objectPojo_then_correctPojoGenerated() {
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator();

    final NonEmptyList<GeneratedFile> generatedFiles =
        pojoGenerator.generatePojo(
            JavaPojos.sampleObjectPojo1(), TestPojoSettings.defaultSettings());

    assertEquals(1, generatedFiles.size());

    expect.toMatchSnapshot(generatedFiles.head().getContent());
  }

  @Test
  @SnapshotName("enumPojo")
  void generatePojo_when_enumPojo_then_correctPojoGenerated() {
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator();

    final NonEmptyList<GeneratedFile> generatedFiles =
        pojoGenerator.generatePojo(JavaPojos.enumPojo(), TestPojoSettings.defaultSettings());

    assertEquals(1, generatedFiles.size());

    expect.toMatchSnapshot(generatedFiles.head().getContent());
  }
}
