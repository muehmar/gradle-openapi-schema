package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.allof;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant.FULL;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant.STANDARD;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.allof.AllOfBuilderGenerator.allOfBuilderGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers;
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
                JavaPojos.objectPojo(JavaPojoMembers.requiredDirectionEnum())),
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
                JavaPojos.objectPojo(JavaPojoMembers.requiredDirectionEnum())),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
