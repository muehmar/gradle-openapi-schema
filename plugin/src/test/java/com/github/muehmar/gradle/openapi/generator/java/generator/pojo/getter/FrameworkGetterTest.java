package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.FrameworkGetter.frameworkGetter;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSetting.NO_JSON;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSetting.NO_VALIDATION;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSettings.getterGeneratorSettings;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.optionalString;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSettings;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.JavaModifier;
import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.ValidationMethods;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SnapshotTest
class FrameworkGetterTest {
  private Expect expect;

  @ParameterizedTest
  @MethodSource("generatorSettings")
  @SnapshotName("generatorSettings")
  void generate_when_generatorSettings_then_matchSnapshot(
      GetterGeneratorSettings generatorSettings) {
    final Generator<JavaPojoMember, PojoSettings> generator = frameworkGetter(generatorSettings);

    final Writer writer;
    writer =
        generator.generate(
            optionalString(),
            defaultTestSettings()
                .withValidationMethods(new ValidationMethods(JavaModifier.PUBLIC, "Raw", true)),
            javaWriter());

    expect
        .scenario(generatorSettings.getSettings().mkString("|"))
        .toMatchSnapshot(writerSnapshot(writer));
  }

  public static Stream<Arguments> generatorSettings() {
    return Stream.of(
        arguments(GetterGeneratorSettings.empty()),
        arguments(new GetterGeneratorSettings(PList.single(NO_JSON))),
        arguments(new GetterGeneratorSettings(PList.single(NO_VALIDATION))));
  }

  @ParameterizedTest
  @MethodSource(
      "com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers#allNecessityAndNullabilityVariantsTestSource")
  @SnapshotName("members")
  void generate_when_members_when_matchSnapshot(JavaPojoMember member) {
    final Generator<JavaPojoMember, PojoSettings> generator =
        frameworkGetter(GetterGeneratorSettings.empty());

    final Writer writer;
    writer = generator.generate(member, defaultTestSettings(), javaWriter());

    expect.scenario(member.getName().asString()).toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  void generate_when_jsonAndValidationDisabled_then_noOutput() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        frameworkGetter(GetterGeneratorSettings.empty());

    final Writer writer;
    writer =
        generator.generate(
            optionalString(),
            defaultTestSettings().withJsonSupport(JsonSupport.NONE).withEnableValidation(false),
            javaWriter());

    assertEquals("", writer.asString());
  }

  @Test
  void generate_when_validationDisabledAndNoJsonGeneratorSettings_then_noOutput() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        frameworkGetter(getterGeneratorSettings(NO_JSON));

    final Writer writer;
    writer =
        generator.generate(
            optionalString(), defaultTestSettings().withEnableValidation(false), javaWriter());

    assertEquals("", writer.asString());
  }

  @Test
  void generate_when_jsonDisabledAndNoValidationGeneratorSettings_then_noOutput() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        frameworkGetter(getterGeneratorSettings(NO_VALIDATION));

    final Writer writer;
    writer =
        generator.generate(
            optionalString(),
            defaultTestSettings().withJsonSupport(JsonSupport.NONE),
            javaWriter());

    assertEquals("", writer.asString());
  }
}
