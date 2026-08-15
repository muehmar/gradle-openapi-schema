package com.github.muehmar.gradle.openapi.generator.java.generator.array;

import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class ArrayPojoGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("arrayPojo")
  void generate_when_arrayPojo_then_correctOutput() {
    final ArrayPojoGenerator arrayPojoGenerator = new ArrayPojoGenerator();
    final Writer writer =
        arrayPojoGenerator.generate(
            JavaPojos.arrayPojo(Constraints.ofUniqueItems(true)),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("arrayPojoWithEnumItem")
  void generate_when_arrayPojoWithEnumItem_then_correctOutput() {
    final ArrayPojoGenerator arrayPojoGenerator = new ArrayPojoGenerator();

    final Writer writer =
        arrayPojoGenerator.generate(
            JavaPojos.arrayPojoWithEnumItem(), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }
}
