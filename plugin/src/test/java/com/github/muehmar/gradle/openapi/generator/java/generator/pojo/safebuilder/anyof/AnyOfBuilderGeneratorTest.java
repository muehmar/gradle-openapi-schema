package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.anyof;

import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@SnapshotTest
class AnyOfBuilderGeneratorTest {
  private Expect expect;

  @ParameterizedTest
  @EnumSource(SafeBuilderVariant.class)
  @SnapshotName("anyOfPojoWithRequiredProperties")
  void generate_when_anyOfPojoWithRequiredProperties_then_correctOutput(
      SafeBuilderVariant variant) {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        AnyOfBuilderGenerator.anyOfBuilderGenerator(variant);
    final JavaObjectPojo anyOfPojo =
        JavaPojos.anyOfPojo(JavaPojos.sampleObjectPojo1(), JavaPojos.sampleObjectPojo2());
    final JavaObjectPojo anyOfPojoWithMembers =
        anyOfPojo.withMembers(
            JavaPojoMembers.fromList(PList.single(TestJavaPojoMembers.requiredNullableString())));
    final Writer writer =
        generator.generate(
            anyOfPojoWithMembers, TestPojoSettings.defaultTestSettings(), javaWriter());

    expect.scenario(variant.name()).toMatchSnapshot(writerSnapshot(writer));
  }

  @ParameterizedTest
  @EnumSource(SafeBuilderVariant.class)
  @SnapshotName("anyOfPojoWithoutRequiredProperties")
  void generate_when_anyOfPojoWithoutRequiredProperties_then_correctOutput(
      SafeBuilderVariant variant) {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        AnyOfBuilderGenerator.anyOfBuilderGenerator(variant);
    final Writer writer =
        generator.generate(
            JavaPojos.anyOfPojo(JavaPojos.sampleObjectPojo1(), JavaPojos.sampleObjectPojo2()),
            TestPojoSettings.defaultTestSettings(),
            javaWriter());

    expect.scenario(variant.name()).toMatchSnapshot(writerSnapshot(writer));
  }
}
