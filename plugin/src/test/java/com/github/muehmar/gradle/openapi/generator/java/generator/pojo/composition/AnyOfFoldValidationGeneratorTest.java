package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static com.github.muehmar.gradle.openapi.SnapshotUtil.writerSnapshot;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static org.junit.jupiter.api.Assertions.*;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.JavaModifier;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class AnyOfFoldValidationGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("AnyOf")
  void generate_when_anyOf_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        AnyOfFoldValidationGenerator.anyOfFoldValidationGenerator();
    final Writer writer =
        generator.generate(
            JavaPojos.anyOfPojo(sampleObjectPojo1(), sampleObjectPojo2()),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("AnyOfProtectedAndDeprecatedSettings")
  void generate_when_anyOfProtectedAndDeprecatedSettings_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        AnyOfFoldValidationGenerator.anyOfFoldValidationGenerator();
    final Writer writer =
        generator.generate(
            JavaPojos.anyOfPojo(sampleObjectPojo1(), sampleObjectPojo2()),
            TestPojoSettings.defaultSettings()
                .withValidationMethods(
                    TestPojoSettings.defaultValidationMethods()
                        .withModifier(JavaModifier.PROTECTED)
                        .withDeprecatedAnnotation(true)),
            Writer.createDefault());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  void generate_when_oneOf_then_noOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        AnyOfFoldValidationGenerator.anyOfFoldValidationGenerator();
    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(sampleObjectPojo1(), sampleObjectPojo2()),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    assertEquals("", writer.asString());
  }
}
