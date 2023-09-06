package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class ValidCountValidationMethodTest {
  private Expect expect;

  @Test
  @SnapshotName("oneOf")
  void generate_when_oneOfPojo_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        ValidCountValidationMethod.validCountValidationMethodGenerator();

    final JavaObjectPojo javaPojo = JavaPojos.oneOfPojo(sampleObjectPojo1(), sampleObjectPojo2());
    final Writer writer = generator.generate(javaPojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("anyOf")
  void generate_when_anyOfPojo_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        ValidCountValidationMethod.validCountValidationMethodGenerator();

    final JavaObjectPojo javaPojo = JavaPojos.anyOfPojo(sampleObjectPojo1(), sampleObjectPojo2());
    final Writer writer = generator.generate(javaPojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  void generate_when_nonComposedPojo_then_noOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        ValidCountValidationMethod.validCountValidationMethodGenerator();

    final JavaObjectPojo javaPojo = JavaPojos.sampleObjectPojo1();
    final Writer writer = generator.generate(javaPojo, defaultTestSettings(), javaWriter());

    assertEquals("", writer.asString());
  }

  @Test
  void generate_when_anyOfPojoWithDisabledValidation_then_noOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        ValidCountValidationMethod.validCountValidationMethodGenerator();

    final JavaObjectPojo javaPojo = JavaPojos.anyOfPojo(sampleObjectPojo1(), sampleObjectPojo2());
    final Writer writer =
        generator.generate(
            javaPojo, defaultTestSettings().withEnableValidation(false), javaWriter());

    assertEquals("", writer.asString());
  }
}
