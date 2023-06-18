package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;

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
class ValidationMethodGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("oneOf")
  void generate_when_oneOf_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        ValidationMethodGenerator.isValidAgainstMethods();
    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(
                sampleObjectPojo1(),
                sampleObjectPojo2(),
                JavaPojos.objectPojo(PList.single(JavaPojoMembers.requiredNullableBirthdate()))),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("anyOf")
  void generate_when_anyOf_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        ValidationMethodGenerator.isValidAgainstMethods();
    final Writer writer =
        generator.generate(
            JavaPojos.anyOfPojo(
                sampleObjectPojo1(),
                sampleObjectPojo2(),
                JavaPojos.objectPojo(PList.single(JavaPojoMembers.requiredNullableBirthdate()))),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());
  }
}
