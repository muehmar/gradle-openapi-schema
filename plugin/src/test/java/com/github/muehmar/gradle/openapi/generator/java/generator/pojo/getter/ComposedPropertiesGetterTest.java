package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import com.github.muehmar.gradle.openapi.generator.java.JacksonRefs;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class ComposedPropertiesGetterTest {
  private Expect expect;

  @Test
  void generate_when_objectMember_then_noOutput() {
    final Generator<JavaPojoMember, PojoSettings> generator = ComposedPropertiesGetter.generator();

    final Writer writer =
        generator.generate(
            JavaPojoMembers.requiredBirthdate(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    assertEquals("", writer.asString());
  }

  @Test
  void generate_when_allOfMember_then_noOutput() {
    final Generator<JavaPojoMember, PojoSettings> generator = ComposedPropertiesGetter.generator();

    final Writer writer =
        generator.generate(
            JavaPojoMembers.requiredBirthdate().asAllOfMember(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    assertEquals("", writer.asString());
  }

  @Test
  void generate_when_anyOfMemberAndNoJsonSupport_then_noOutput() {
    final Generator<JavaPojoMember, PojoSettings> generator = ComposedPropertiesGetter.generator();

    final Writer writer =
        generator.generate(
            JavaPojoMembers.requiredBirthdate().asAnyOfMember(),
            TestPojoSettings.defaultSettings().withJsonSupport(JsonSupport.NONE),
            Writer.createDefault());

    assertEquals("", writer.asString());
  }

  @Test
  @SnapshotName("requiredOneOfMember")
  void generate_when_requiredOneOfMember_then_correctOutput() {
    final Generator<JavaPojoMember, PojoSettings> generator = ComposedPropertiesGetter.generator();

    final Writer writer =
        generator.generate(
            JavaPojoMembers.requiredBirthdate().asOneOfMember(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_TIME_LOCAL_DATE::equals));
  }

  @Test
  @SnapshotName("requiredAnyOfMember")
  void generate_when_requiredAnyOfMember_then_correctOutput() {
    final Generator<JavaPojoMember, PojoSettings> generator = ComposedPropertiesGetter.generator();

    final Writer writer =
        generator.generate(
            JavaPojoMembers.requiredBirthdate().asAnyOfMember(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_TIME_LOCAL_DATE::equals));
  }

  @Test
  @SnapshotName("requiredNullableOneOfMember")
  void generate_when_requiredNullableOneOfMember_then_correctOutput() {
    final Generator<JavaPojoMember, PojoSettings> generator = ComposedPropertiesGetter.generator();

    final Writer writer =
        generator.generate(
            JavaPojoMembers.requiredNullableBirthdate().asOneOfMember(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_TIME_LOCAL_DATE::equals));
  }

  @Test
  @SnapshotName("requiredNullableAnyOfMember")
  void generate_when_requiredNullableAnyOfMember_then_correctOutput() {
    final Generator<JavaPojoMember, PojoSettings> generator = ComposedPropertiesGetter.generator();

    final Writer writer =
        generator.generate(
            JavaPojoMembers.requiredNullableBirthdate().asAnyOfMember(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_TIME_LOCAL_DATE::equals));
  }

  @Test
  @SnapshotName("optionalOneOfMember")
  void generate_when_optionalOneOfMember_then_correctOutput() {
    final Generator<JavaPojoMember, PojoSettings> generator = ComposedPropertiesGetter.generator();

    final Writer writer =
        generator.generate(
            JavaPojoMembers.optionalBirthdate().asOneOfMember(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_TIME_LOCAL_DATE::equals));
    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_INCLUDE::equals));
  }

  @Test
  @SnapshotName("optionalAnyOfMember")
  void generate_when_optionalAnyOfMember_then_correctOutput() {
    final Generator<JavaPojoMember, PojoSettings> generator = ComposedPropertiesGetter.generator();

    final Writer writer =
        generator.generate(
            JavaPojoMembers.optionalBirthdate().asOneOfMember(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_TIME_LOCAL_DATE::equals));
    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_INCLUDE::equals));
  }

  @Test
  @SnapshotName("optionalNullableOneOfMember")
  void generate_when_optionalNullableOneOfMember_then_correctOutput() {
    final Generator<JavaPojoMember, PojoSettings> generator = ComposedPropertiesGetter.generator();

    final Writer writer =
        generator.generate(
            JavaPojoMembers.optionalNullableBirthdate().asOneOfMember(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_TIME_LOCAL_DATE::equals));
    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_INCLUDE::equals));
    assertTrue(writer.getRefs().exists(OpenApiUtilRefs.JACKSON_NULL_CONTAINER::equals));
  }

  @Test
  @SnapshotName("optionalNullableAnyOfMember")
  void generate_when_optionalNullableAnyOfMember_then_correctOutput() {
    final Generator<JavaPojoMember, PojoSettings> generator = ComposedPropertiesGetter.generator();

    final Writer writer =
        generator.generate(
            JavaPojoMembers.optionalNullableBirthdate().asOneOfMember(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_TIME_LOCAL_DATE::equals));
    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_INCLUDE::equals));
    assertTrue(writer.getRefs().exists(OpenApiUtilRefs.JACKSON_NULL_CONTAINER::equals));
  }
}
