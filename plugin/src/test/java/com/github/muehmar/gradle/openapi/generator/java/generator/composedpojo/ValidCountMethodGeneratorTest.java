package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static org.junit.jupiter.api.Assertions.assertEquals;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class ValidCountMethodGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("oneOf")
  void generate_when_oneOfPojo_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        ValidCountMethodGenerator.validCountMethod();
    final JavaObjectPojo pojo = JavaPojos.oneOfPojo(sampleObjectPojo1(), sampleObjectPojo2());

    final Writer writer =
        generator.generate(pojo, TestPojoSettings.defaultSettings(), Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("anyOf")
  void generate_when_anyOfPojo_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        ValidCountMethodGenerator.validCountMethod();
    final JavaObjectPojo pojo = JavaPojos.anyOfPojo(sampleObjectPojo1(), sampleObjectPojo2());

    final Writer writer =
        generator.generate(pojo, TestPojoSettings.defaultSettings(), Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  void generate_when_nonComposedPojo_then_noOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        ValidCountMethodGenerator.validCountMethod();
    final JavaObjectPojo pojo = JavaPojos.sampleObjectPojo1();

    final Writer writer =
        generator.generate(pojo, TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals("", writer.asString());
  }
}
