package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.allof;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.StagedBuilderVariant.FULL;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.StagedBuilderVariant.STANDARD;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.allof.AllOfBuilderGenerator.allOfBuilderGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredDirectionEnum;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

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
class AllOfBuilderGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("allOfPojo")
  void generate_when_allOfPojo_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = allOfBuilderGenerator(STANDARD);

    final Writer writer =
        generator.generate(
            JavaPojos.allOfPojo(
                sampleObjectPojo1(),
                sampleObjectPojo2(),
                JavaPojos.objectPojo(requiredDirectionEnum())),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("allOfPojoWithCompositionsInAllOfSubPojos")
  void generate_when_allOfPojoWithCompositionsInAllOfSubPojos_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = allOfBuilderGenerator(STANDARD);

    final Writer writer =
        generator.generate(
            JavaPojos.allOfPojo(
                sampleObjectPojo1(),
                sampleObjectPojo2(),
                JavaPojos.oneOfPojo(JavaPojos.objectPojo(requiredDirectionEnum()))),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("allOfPojoWithAllNecessityAndNullabilityVariants")
  void generate_when_allOfPojoWithAllNecessityAndNullabilityVariants_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = allOfBuilderGenerator(STANDARD);

    final Writer writer =
        generator.generate(
            JavaPojos.allOfPojo(JavaPojos.allNecessityAndNullabilityVariants()),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("allOfPojoFullBuilder")
  void generate_when_allOfPojoFullBuilder_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = allOfBuilderGenerator(FULL);

    final Writer writer =
        generator.generate(
            JavaPojos.allOfPojo(
                sampleObjectPojo1(),
                sampleObjectPojo2(),
                JavaPojos.objectPojo(requiredDirectionEnum())),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
