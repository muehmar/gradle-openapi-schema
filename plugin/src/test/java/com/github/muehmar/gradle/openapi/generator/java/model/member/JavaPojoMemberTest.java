package com.github.muehmar.gradle.openapi.generator.java.model.member;

import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredBirthdate;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredDouble;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.REQUIRED;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.settings.GetterSuffixes;
import com.github.muehmar.gradle.openapi.generator.settings.GetterSuffixesBuilder;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

class JavaPojoMemberTest {
  private static final GetterSuffixes GETTER_SUFFIXES =
      GetterSuffixesBuilder.create()
          .requiredSuffix("Required")
          .requiredNullableSuffix("Nullable")
          .optionalSuffix("Optional")
          .optionalNullableSuffix("Tristate")
          .build();

  @Test
  void getGetterName_when_called_then_correctGetter() {
    final JavaPojoMember javaPojoMember = TestJavaPojoMembers.requiredString();
    assertEquals("getStringVal", javaPojoMember.getGetterName().asString());
  }

  @Test
  void getWitherName_when_called_then_correctGetter() {
    final JavaPojoMember javaPojoMember = TestJavaPojoMembers.requiredString();
    assertEquals("withStringVal", javaPojoMember.getWitherName().asString());
  }

  @ParameterizedTest
  @MethodSource("getterNameWithSuffix")
  void getGetterNameWithSuffix_when_calledForAllVariants_then_suffixedWithCorrespondingGetterSuffix(
      Necessity necessity, Nullability nullability, String getterName) {
    final JavaPojoMember javaPojoMember = TestJavaPojoMembers.string(necessity, nullability);

    assertEquals(
        getterName,
        javaPojoMember
            .getGetterNameWithSuffix(defaultTestSettings().withGetterSuffixes(GETTER_SUFFIXES))
            .asString());
  }

  public static Stream<Arguments> getterNameWithSuffix() {
    return Stream.of(
        Arguments.of(Necessity.REQUIRED, Nullability.NOT_NULLABLE, "getStringValRequired"),
        Arguments.of(Necessity.REQUIRED, NULLABLE, "getStringValNullable"),
        Arguments.of(OPTIONAL, Nullability.NOT_NULLABLE, "getStringValOptional"),
        Arguments.of(OPTIONAL, NULLABLE, "getStringValTristate"));
  }

  @ParameterizedTest
  @CsvSource({"is, isStringVal", "get, getStringVal", "set, setStringVal", "'', stringVal"})
  void prefixedMethodName_when_called_then_matchExpectedMethodName(
      String prefix, String expectedMethodName) {
    final JavaPojoMember member = TestJavaPojoMembers.requiredString();
    assertEquals(expectedMethodName, member.prefixedMethodName(prefix).asString());
  }

  @Test
  void getValidationGetterName_when_calledWithDefaultSettings_then_correctMethodName() {
    JavaPojoMember member = TestJavaPojoMembers.requiredString();
    JavaName validationGetterName = member.getValidationGetterName(defaultTestSettings());
    assertEquals("getStringValRaw", validationGetterName.asString());
  }

  @Test
  void getIsNullFlagName_when_called_then_correctMethodName() {
    JavaPojoMember member = TestJavaPojoMembers.requiredString();
    JavaName validationGetterName = member.getIsNullFlagName();
    assertEquals("isStringValNull", validationGetterName.asString());
  }

  @Test
  void getIsPresentFlagName_when_called_then_correctMethodName() {
    JavaPojoMember member = TestJavaPojoMembers.requiredString();
    JavaName validationGetterName = member.getIsPresentFlagName();
    assertEquals("isStringValPresent", validationGetterName.asString());
  }

  @ParameterizedTest
  @MethodSource("membersForCreatingTechnicalMembers")
  void getTechnicalMembers_when_called_then_matchExpectedFieldNames(
      JavaPojoMember member, String expected) {

    final PList<TechnicalPojoMember> technicalMembers = member.getTechnicalMembers();

    assertEquals(expected, technicalMembers.map(TechnicalPojoMember::getName).mkString(","));
  }

  public static Stream<Arguments> membersForCreatingTechnicalMembers() {
    return Stream.of(
        arguments(requiredBirthdate(), "birthdate"),
        arguments(
            TestJavaPojoMembers.optionalString(), "optionalStringVal,isOptionalStringValNotNull"),
        arguments(
            TestJavaPojoMembers.requiredNullableString(),
            "requiredNullableStringVal,isRequiredNullableStringValPresent"),
        arguments(
            TestJavaPojoMembers.optionalNullableString(),
            "optionalNullableStringVal,isOptionalNullableStringValNull"));
  }

  @Test
  void asInnerEnumOf_when_calledForStringMember_then_unchanged() {
    final JavaPojoMember member = TestJavaPojoMembers.requiredString();
    assertEquals(member, member.asInnerEnumOf(JavaName.fromString("AdminDto")));
  }

  @Test
  void asInnerEnumOf_when_calledForEnumMember_then_classNameReferencedWithOuterClass() {
    final JavaPojoMember member = TestJavaPojoMembers.requiredColorEnum();
    final JavaPojoMember mappedMember = member.asInnerEnumOf(JavaName.fromString("AdminDto"));
    assertEquals(
        "AdminDto.Color",
        mappedMember.getJavaType().getQualifiedClassName().getClassName().asString());
  }

  @ParameterizedTest
  @MethodSource("mergeToLeastRestrictiveArguments")
  void mergeToLeastRestrictive_when_arguments_then_matchExpectedMergedMember(
      JavaPojoMember originalMember,
      JavaPojoMember memberToMerge,
      Optional<JavaPojoMember> expectedMergedMember) {
    final Optional<JavaPojoMember> mergedMember =
        originalMember.mergeToLeastRestrictive(memberToMerge);

    assertEquals(expectedMergedMember, mergedMember);
  }

  private static Stream<Arguments> mergeToLeastRestrictiveArguments() {
    final JavaPojoMember birthdate = requiredBirthdate();
    return Stream.of(
        arguments(birthdate, requiredDouble(), Optional.empty()),
        arguments(birthdate, birthdate.withDescription("Other desc"), Optional.of(birthdate)),
        arguments(
            birthdate,
            birthdate.withDescription("Other desc").withNullability(NULLABLE),
            Optional.of(birthdate.withNullability(NULLABLE))),
        arguments(
            birthdate,
            birthdate.withDescription("Other desc").withNecessity(OPTIONAL),
            Optional.of(birthdate.withNecessity(OPTIONAL))));
  }

  @ParameterizedTest
  @MethodSource("mergeToMostRestrictiveArguments")
  void mergeToMostRestrictive_when_arguments_then_matchExpectedMergedMember(
      JavaPojoMember originalMember,
      JavaPojoMember memberToMerge,
      Optional<JavaPojoMember> expectedMergedMember) {
    final Optional<JavaPojoMember> mergedMember =
        originalMember.mergeToMostRestrictive(memberToMerge);

    assertEquals(expectedMergedMember, mergedMember);
  }

  private static Stream<Arguments> mergeToMostRestrictiveArguments() {
    final JavaPojoMember birthdate =
        requiredBirthdate().withNullability(NULLABLE).withNecessity(OPTIONAL);
    return Stream.of(
        arguments(birthdate, requiredDouble(), Optional.empty()),
        arguments(birthdate, birthdate.withDescription("Other desc"), Optional.of(birthdate)),
        arguments(
            birthdate,
            birthdate.withDescription("Other desc").withNullability(NOT_NULLABLE),
            Optional.of(birthdate.withNullability(NOT_NULLABLE))),
        arguments(
            birthdate,
            birthdate.withDescription("Other desc").withNecessity(REQUIRED),
            Optional.of(birthdate.withNecessity(REQUIRED))));
  }
}
