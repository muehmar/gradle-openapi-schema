package com.github.muehmar.gradle.openapi.generator.java.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.settings.GetterSuffixes;
import com.github.muehmar.gradle.openapi.generator.settings.GetterSuffixesBuilder;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
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
    final JavaPojoMember javaPojoMember = JavaPojoMembers.requiredString();
    assertEquals("getStringVal", javaPojoMember.getGetterName().asString());
  }

  @Test
  void getWitherName_when_called_then_correctGetter() {
    final JavaPojoMember javaPojoMember = JavaPojoMembers.requiredString();
    assertEquals("withStringVal", javaPojoMember.getWitherName().asString());
  }

  @ParameterizedTest
  @MethodSource("getterNameWithSuffix")
  void getGetterNameWithSuffix_when_calledForAllVariants_then_suffixedWithCorrespondingGetterSuffix(
      Necessity necessity, Nullability nullability, String getterName) {
    final JavaPojoMember javaPojoMember = JavaPojoMembers.string(necessity, nullability);

    assertEquals(
        getterName,
        javaPojoMember
            .getGetterNameWithSuffix(
                TestPojoSettings.defaultSettings().withGetterSuffixes(GETTER_SUFFIXES))
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
    final JavaPojoMember member = JavaPojoMembers.requiredString();
    assertEquals(expectedMethodName, member.prefixedMethodName(prefix).asString());
  }

  @Test
  void getValidationGetterName_when_calledWithDefaultSettings_then_correctMethodName() {
    JavaPojoMember member = JavaPojoMembers.requiredString();
    JavaIdentifier validationGetterName =
        member.getValidationGetterName(TestPojoSettings.defaultSettings());
    assertEquals("getStringValRaw", validationGetterName.asString());
  }

  @Test
  void getIsNullFlagName_when_called_then_correctMethodName() {
    JavaPojoMember member = JavaPojoMembers.requiredString();
    JavaIdentifier validationGetterName = member.getIsNullFlagName();
    assertEquals("isStringValNull", validationGetterName.asString());
  }

  @Test
  void getIsPresentFlagName_when_called_then_correctMethodName() {
    JavaPojoMember member = JavaPojoMembers.requiredString();
    JavaIdentifier validationGetterName = member.getIsPresentFlagName();
    assertEquals("isStringValPresent", validationGetterName.asString());
  }

  @ParameterizedTest
  @MethodSource("membersForCreatingFieldNames")
  void createFieldNames_when_called_then_matchExpectedFieldNames(
      JavaPojoMember member, String expected) {
    PList<JavaIdentifier> fieldNames = member.createFieldNames();
    assertEquals(expected, fieldNames.mkString(","));
  }

  public static Stream<Arguments> membersForCreatingFieldNames() {
    return Stream.of(
        arguments(JavaPojoMembers.requiredBirthdate(), "birthdate"),
        arguments(JavaPojoMembers.optionalString(), "optionalStringVal"),
        arguments(
            JavaPojoMembers.requiredNullableString(),
            "requiredNullableStringVal,isRequiredNullableStringValPresent"),
        arguments(
            JavaPojoMembers.optionalNullableString(),
            "optionalNullableStringVal,isOptionalNullableStringValNull"));
  }
}
