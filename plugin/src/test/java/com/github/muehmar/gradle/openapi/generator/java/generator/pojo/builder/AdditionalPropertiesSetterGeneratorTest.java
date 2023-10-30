package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.AdditionalPropertiesSetterGenerator.additionalPropertiesSetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMembers.requiredBirthdate;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes.objectType;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class AdditionalPropertiesSetterGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("objectPojoAnyTypeAdditionalProperties")
  void generate_when_objectPojoAnyTypeAdditionalProperties_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = additionalPropertiesSetterGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.objectPojo(requiredBirthdate()), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("objectPojoSpecificTypeAdditionalProperties")
  void generate_when_objectPojoSpecificTypeAdditionalProperties_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = additionalPropertiesSetterGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.objectPojo(
                PList.single(requiredBirthdate()),
                JavaAdditionalProperties.allowedFor(objectType())),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
