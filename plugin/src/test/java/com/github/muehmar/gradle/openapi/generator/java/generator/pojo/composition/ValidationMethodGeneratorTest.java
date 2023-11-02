package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@SnapshotTest
class ValidationMethodGeneratorTest {
  private Expect expect;

  @ParameterizedTest
  @SnapshotName("oneOf")
  @ValueSource(booleans = {true, false})
  void generate_when_oneOf_then_correctOutput(boolean validationEnabled) {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        ValidationMethodGenerator.validationMethodGenerator();
    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(
                sampleObjectPojo1(),
                sampleObjectPojo2(),
                JavaPojos.objectPojo(PList.single(JavaPojoMembers.requiredNullableString()))),
            defaultTestSettings().withEnableValidation(validationEnabled),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @ParameterizedTest
  @SnapshotName("anyOf")
  @ValueSource(booleans = {true, false})
  void generate_when_anyOf_then_correctOutput(boolean validationEnabled) {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        ValidationMethodGenerator.validationMethodGenerator();
    final Writer writer =
        generator.generate(
            JavaPojos.anyOfPojo(
                sampleObjectPojo1(),
                sampleObjectPojo2(),
                JavaPojos.objectPojo(PList.single(JavaPojoMembers.requiredNullableString()))),
            defaultTestSettings().withEnableValidation(validationEnabled),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("nestedOneOf")
  void generate_when_nestedOneOf_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        ValidationMethodGenerator.validationMethodGenerator();
    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(
                JavaPojos.oneOfPojo(sampleObjectPojo1(), sampleObjectPojo2()),
                JavaPojos.objectPojo(PList.single(JavaPojoMembers.requiredNullableString()))),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("composedPojoHasNoRequiredMembers")
  void generate_when_composedPojoHasNoRequiredMembers_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        ValidationMethodGenerator.validationMethodGenerator();
    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(JavaPojos.objectPojo(JavaPojoMembers.optionalBirthdate())),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
