package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.RequiredMemberBuilderGenerator.requiredMemberBuilderGenerator;
import static org.junit.jupiter.api.Assertions.assertEquals;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
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
class RequiredMemberBuilderGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("allNecessityAndNullabilityVariants")
  void generate_when_allNecessityAndNullabilityVariants_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> gen = requiredMemberBuilderGenerator();

    final Writer writer =
        gen.generate(
            JavaPojos.allNecessityAndNullabilityVariants(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("builderMethodsOfFirstRequiredMemberGeneratorWithRequiredProperty")
  void builderMethodsOfFirstRequiredMemberGenerator_when_hasRequiredProperty_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> gen =
        RequiredMemberBuilderGenerator.builderMethodsOfFirstRequiredMemberGenerator();

    final Writer writer =
        gen.generate(
            JavaPojos.objectPojo(JavaPojoMembers.requiredNullableBirthdate()),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  void builderMethodsOfFirstRequiredMemberGenerator_when_hasNoRequiredProperty_then_noOutput() {
    final Generator<JavaObjectPojo, PojoSettings> gen =
        RequiredMemberBuilderGenerator.builderMethodsOfFirstRequiredMemberGenerator();

    final Writer writer =
        gen.generate(
            JavaPojos.objectPojo(JavaPojoMembers.optionalBirthdate()),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    assertEquals("", writer.asString());
  }
}
