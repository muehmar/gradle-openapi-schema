package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.anyof;

import static com.github.muehmar.gradle.openapi.SnapshotUtil.writerSnapshot;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class AnyOfBuilderGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("anyOfPojoWithRequiredProperties")
  void generate_when_anyOfPojoWithRequiredProperties_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        AnyOfBuilderGenerator.anyOfBuilderGenerator();
    final JavaObjectPojo anyOfPojo =
        JavaPojos.anyOfPojo(JavaPojos.sampleObjectPojo1(), JavaPojos.sampleObjectPojo2());
    final JavaObjectPojo anyOfPojoWithMembers =
        JavaPojos.withMembers(anyOfPojo, PList.single(JavaPojoMembers.requiredNullableString()));
    final Writer writer =
        generator.generate(
            anyOfPojoWithMembers, TestPojoSettings.defaultSettings(), Writer.createDefault());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("anyOfPojoWithoutRequiredProperties")
  void generate_when_anyOfPojoWithoutRequiredProperties_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        AnyOfBuilderGenerator.anyOfBuilderGenerator();
    final Writer writer =
        generator.generate(
            JavaPojos.anyOfPojo(JavaPojos.sampleObjectPojo1(), JavaPojos.sampleObjectPojo2()),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
