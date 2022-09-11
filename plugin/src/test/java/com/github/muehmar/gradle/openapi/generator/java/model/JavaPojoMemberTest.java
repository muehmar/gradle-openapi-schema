package com.github.muehmar.gradle.openapi.generator.java.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.settings.GetterSuffixes;
import com.github.muehmar.gradle.openapi.generator.settings.GetterSuffixesBuilder;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
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
}
