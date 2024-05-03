package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter;

import static com.github.muehmar.gradle.openapi.generator.model.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.REQUIRED;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.SetterModifier.SetterJavaType;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.StagedBuilderSettingsBuilder;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeConversion;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import io.github.muehmar.codegenerator.java.JavaModifier;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SetterModifierTest {

  @ParameterizedTest
  @MethodSource("allMemberAndSettingsAndSetterTypeVariants")
  void forMember_when_allMemberAndSettingsAndSetterTypeVariants_then_matchExpectedModifier(
      JavaPojoMember member, PojoSettings settings, SetterJavaType type, JavaModifier expected) {
    final JavaModifier javaModifier = SetterModifier.forMember(member, settings, type);

    assertEquals(expected, javaModifier);
  }

  public static Stream<Arguments> allMemberAndSettingsAndSetterTypeVariants() {
    final PojoSettings enabledStagedBuilder =
        TestPojoSettings.defaultTestSettings()
            .withStagedBuilder(
                StagedBuilderSettingsBuilder.stagedBuilderSettingsBuilder().enabled(true).build());
    final PojoSettings disabledStagedBuilder =
        TestPojoSettings.defaultTestSettings()
            .withStagedBuilder(
                StagedBuilderSettingsBuilder.stagedBuilderSettingsBuilder().enabled(false).build());

    final JavaPojoMember member = TestJavaPojoMembers.requiredString();

    final ClassTypeMapping classTypeMapping =
        new ClassTypeMapping(
            "String",
            "com.custom.String",
            Optional.of(new TypeConversion("asString", "toCustomString")));
    final JavaType stringTypeWithApiType =
        JavaType.wrap(
            StringType.noFormat(), TypeMappings.ofSingleClassTypeMapping(classTypeMapping));
    final JavaType stringType = JavaType.wrap(StringType.noFormat(), TypeMappings.empty());

    return Stream.of(
        arguments(
            member.withNecessity(REQUIRED).withJavaType(stringType),
            enabledStagedBuilder,
            SetterJavaType.DEFAULT,
            PRIVATE),
        arguments(
            member.withNecessity(REQUIRED).withJavaType(stringType),
            disabledStagedBuilder,
            SetterJavaType.DEFAULT,
            PUBLIC),
        arguments(
            member.withNecessity(OPTIONAL).withJavaType(stringType),
            enabledStagedBuilder,
            SetterJavaType.DEFAULT,
            PUBLIC),
        arguments(
            member.withNecessity(OPTIONAL).withJavaType(stringType),
            disabledStagedBuilder,
            SetterJavaType.DEFAULT,
            PUBLIC),
        arguments(
            member.withNecessity(REQUIRED).withJavaType(stringTypeWithApiType),
            enabledStagedBuilder,
            SetterJavaType.DEFAULT,
            PRIVATE),
        arguments(
            member.withNecessity(REQUIRED).withJavaType(stringTypeWithApiType),
            disabledStagedBuilder,
            SetterJavaType.DEFAULT,
            PRIVATE),
        arguments(
            member.withNecessity(OPTIONAL).withJavaType(stringTypeWithApiType),
            enabledStagedBuilder,
            SetterJavaType.DEFAULT,
            PRIVATE),
        arguments(
            member.withNecessity(OPTIONAL).withJavaType(stringTypeWithApiType),
            disabledStagedBuilder,
            SetterJavaType.DEFAULT,
            PRIVATE),
        arguments(
            member.withNecessity(REQUIRED).withJavaType(stringTypeWithApiType),
            enabledStagedBuilder,
            SetterJavaType.API,
            PRIVATE),
        arguments(
            member.withNecessity(REQUIRED).withJavaType(stringTypeWithApiType),
            disabledStagedBuilder,
            SetterJavaType.API,
            PUBLIC),
        arguments(
            member.withNecessity(OPTIONAL).withJavaType(stringTypeWithApiType),
            enabledStagedBuilder,
            SetterJavaType.API,
            PUBLIC),
        arguments(
            member.withNecessity(OPTIONAL).withJavaType(stringTypeWithApiType),
            disabledStagedBuilder,
            SetterJavaType.API,
            PUBLIC));
  }
}
