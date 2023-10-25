package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class ComposedPropertiesGetterTest {
  private Expect expect;

  @Test
  void generate_when_objectMember_then_noOutput() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        ComposedPropertiesGetter.composedPropertiesGetterGenerator();

    final Writer writer =
        generator.generate(
            JavaPojoMembers.requiredBirthdate(), defaultTestSettings(), javaWriter());

    assertEquals("", writer.asString());
  }

  @Test
  void generate_when_allOfMember_then_noOutput() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        ComposedPropertiesGetter.composedPropertiesGetterGenerator();

    final Writer writer =
        generator.generate(
            JavaPojoMembers.requiredBirthdate().asAllOfMember(),
            defaultTestSettings(),
            javaWriter());

    assertEquals("", writer.asString());
  }

  @Test
  @SnapshotName("anyOfMemberAndNoJsonSupport")
  void generate_when_anyOfMemberAndNoJsonSupport_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        ComposedPropertiesGetter.composedPropertiesGetterGenerator();

    final Writer writer =
        generator.generate(
            JavaPojoMembers.requiredBirthdate().asAnyOfMember(),
            defaultTestSettings().withJsonSupport(JsonSupport.NONE),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("requiredOneOfMember")
  void generate_when_requiredOneOfMember_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        ComposedPropertiesGetter.composedPropertiesGetterGenerator();

    final Writer writer =
        generator.generate(
            JavaPojoMembers.requiredBirthdate().asOneOfMember(),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("requiredAnyOfMember")
  void generate_when_requiredAnyOfMember_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        ComposedPropertiesGetter.composedPropertiesGetterGenerator();

    final Writer writer =
        generator.generate(
            JavaPojoMembers.requiredBirthdate().asAnyOfMember(),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("requiredNullableOneOfMember")
  void generate_when_requiredNullableOneOfMember_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        ComposedPropertiesGetter.composedPropertiesGetterGenerator();

    final Writer writer =
        generator.generate(
            JavaPojoMembers.requiredNullableBirthdate().asOneOfMember(),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("requiredNullableAnyOfMember")
  void generate_when_requiredNullableAnyOfMember_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        ComposedPropertiesGetter.composedPropertiesGetterGenerator();

    final Writer writer =
        generator.generate(
            JavaPojoMembers.requiredNullableBirthdate().asAnyOfMember(),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("optionalOneOfMember")
  void generate_when_optionalOneOfMember_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        ComposedPropertiesGetter.composedPropertiesGetterGenerator();

    final Writer writer =
        generator.generate(
            JavaPojoMembers.optionalBirthdate().asOneOfMember(),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("optionalAnyOfMember")
  void generate_when_optionalAnyOfMember_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        ComposedPropertiesGetter.composedPropertiesGetterGenerator();

    final Writer writer =
        generator.generate(
            JavaPojoMembers.optionalBirthdate().asOneOfMember(),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("optionalNullableOneOfMember")
  void generate_when_optionalNullableOneOfMember_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        ComposedPropertiesGetter.composedPropertiesGetterGenerator();

    final Writer writer =
        generator.generate(
            JavaPojoMembers.optionalNullableBirthdate().asOneOfMember(),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("optionalNullableAnyOfMember")
  void generate_when_optionalNullableAnyOfMember_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        ComposedPropertiesGetter.composedPropertiesGetterGenerator();

    final Writer writer =
        generator.generate(
            JavaPojoMembers.optionalNullableBirthdate().asOneOfMember(),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
