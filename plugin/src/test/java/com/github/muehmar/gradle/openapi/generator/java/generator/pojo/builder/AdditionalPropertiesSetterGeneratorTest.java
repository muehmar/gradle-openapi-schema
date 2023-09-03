package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder;

import static com.github.muehmar.gradle.openapi.SnapshotUtil.writerSnapshot;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.AdditionalPropertiesSetterGenerator.additionalPropertiesSetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers.requiredBirthdate;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes.objectType;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
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
