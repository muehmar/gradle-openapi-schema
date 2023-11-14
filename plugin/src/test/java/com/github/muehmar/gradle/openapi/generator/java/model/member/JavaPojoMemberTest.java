package com.github.muehmar.gradle.openapi.generator.java.model.member;

import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.settings.GetterSuffixes;
import com.github.muehmar.gradle.openapi.generator.settings.GetterSuffixesBuilder;
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
        Arguments.of(Necessity.REQUIRED, Nullability.NULLABLE, "getStringValNullable"),
        Arguments.of(Necessity.OPTIONAL, Nullability.NOT_NULLABLE, "getStringValOptional"),
        Arguments.of(Necessity.OPTIONAL, Nullability.NULLABLE, "getStringValTristate"));
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
        arguments(TestJavaPojoMembers.requiredBirthdate(), "birthdate"),
        arguments(TestJavaPojoMembers.optionalString(), "optionalStringVal"),
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
}
