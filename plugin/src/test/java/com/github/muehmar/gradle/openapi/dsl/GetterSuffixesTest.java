package com.github.muehmar.gradle.openapi.dsl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class GetterSuffixesTest {
  @Test
  void withCommonSuffixes_when_onlyRequiredCommonSuffix_then_commonSuffixUsed() {
    final GetterSuffixes getterSuffixes = GetterSuffixes.allUndefined();
    final GetterSuffixes commonSuffixes = GetterSuffixes.allUndefined().withRequiredSuffix("Req");

    final GetterSuffixes deviatedSuffixes = getterSuffixes.withCommonSuffixes(commonSuffixes);

    assertEquals(commonSuffixes.getRequiredSuffix(), deviatedSuffixes.getRequiredSuffix());
    assertEquals(Optional.empty(), deviatedSuffixes.getRequiredNullableSuffix());
    assertEquals(Optional.empty(), deviatedSuffixes.getOptionalSuffix());
    assertEquals(Optional.empty(), deviatedSuffixes.getOptionalNullableSuffix());
  }

  @Test
  void withCommonSuffixes_when_specificRequiredSuffixDefined_then_specificSuffixUsed() {
    final GetterSuffixes getterSuffixes = GetterSuffixes.allUndefined().withRequiredSuffix("Req1");
    final GetterSuffixes commonSuffixes = GetterSuffixes.allUndefined().withRequiredSuffix("Req2");

    final GetterSuffixes deviatedSuffixes = getterSuffixes.withCommonSuffixes(commonSuffixes);

    assertEquals(getterSuffixes.getRequiredSuffix(), deviatedSuffixes.getRequiredSuffix());
    assertEquals(Optional.empty(), deviatedSuffixes.getRequiredNullableSuffix());
    assertEquals(Optional.empty(), deviatedSuffixes.getOptionalSuffix());
    assertEquals(Optional.empty(), deviatedSuffixes.getOptionalNullableSuffix());
  }

  @Test
  void withCommonSuffixes_when_onlyRequiredNullableCommonSuffix_then_commonSuffixUsed() {
    final GetterSuffixes getterSuffixes = GetterSuffixes.allUndefined();
    final GetterSuffixes commonSuffixes =
        GetterSuffixes.allUndefined().withRequiredNullableSuffix("ReqNull");

    final GetterSuffixes deviatedSuffixes = getterSuffixes.withCommonSuffixes(commonSuffixes);

    assertEquals(Optional.empty(), deviatedSuffixes.getRequiredSuffix());
    assertEquals(
        commonSuffixes.getRequiredNullableSuffix(), deviatedSuffixes.getRequiredNullableSuffix());
    assertEquals(Optional.empty(), deviatedSuffixes.getOptionalSuffix());
    assertEquals(Optional.empty(), deviatedSuffixes.getOptionalNullableSuffix());
  }

  @Test
  void withCommonSuffixes_when_specificRequiredNullableSuffixDefined_then_specificSuffixUsed() {
    final GetterSuffixes getterSuffixes =
        GetterSuffixes.allUndefined().withRequiredNullableSuffix("ReqNull1");
    final GetterSuffixes commonSuffixes =
        GetterSuffixes.allUndefined().withRequiredNullableSuffix("ReqNull2");

    final GetterSuffixes deviatedSuffixes = getterSuffixes.withCommonSuffixes(commonSuffixes);

    assertEquals(Optional.empty(), deviatedSuffixes.getRequiredSuffix());
    assertEquals(
        getterSuffixes.getRequiredNullableSuffix(), deviatedSuffixes.getRequiredNullableSuffix());
    assertEquals(Optional.empty(), deviatedSuffixes.getOptionalSuffix());
    assertEquals(Optional.empty(), deviatedSuffixes.getOptionalNullableSuffix());
  }

  @Test
  void withCommonSuffixes_when_onlyOptionalCommonSuffix_then_commonSuffixUsed() {
    final GetterSuffixes getterSuffixes = GetterSuffixes.allUndefined();
    final GetterSuffixes commonSuffixes = GetterSuffixes.allUndefined().withOptionalSuffix("Opt");

    final GetterSuffixes deviatedSuffixes = getterSuffixes.withCommonSuffixes(commonSuffixes);

    assertEquals(Optional.empty(), deviatedSuffixes.getRequiredSuffix());
    assertEquals(Optional.empty(), deviatedSuffixes.getRequiredNullableSuffix());
    assertEquals(commonSuffixes.getOptionalSuffix(), deviatedSuffixes.getOptionalSuffix());
    assertEquals(Optional.empty(), deviatedSuffixes.getOptionalNullableSuffix());
  }

  @Test
  void withCommonSuffixes_when_specificOptionalSuffixDefined_then_specificSuffixUsed() {
    final GetterSuffixes getterSuffixes = GetterSuffixes.allUndefined().withOptionalSuffix("Opt1");
    final GetterSuffixes commonSuffixes = GetterSuffixes.allUndefined().withOptionalSuffix("Opt2");

    final GetterSuffixes deviatedSuffixes = getterSuffixes.withCommonSuffixes(commonSuffixes);

    assertEquals(Optional.empty(), deviatedSuffixes.getRequiredSuffix());
    assertEquals(Optional.empty(), deviatedSuffixes.getRequiredNullableSuffix());
    assertEquals(getterSuffixes.getOptionalSuffix(), deviatedSuffixes.getOptionalSuffix());
    assertEquals(Optional.empty(), deviatedSuffixes.getOptionalNullableSuffix());
  }

  @Test
  void withCommonSuffixes_when_onlyOptionalNullableCommonSuffix_then_commonSuffixUsed() {
    final GetterSuffixes getterSuffixes = GetterSuffixes.allUndefined();
    final GetterSuffixes commonSuffixes =
        GetterSuffixes.allUndefined().withOptionalNullableSuffix("OptNull");

    final GetterSuffixes deviatedSuffixes = getterSuffixes.withCommonSuffixes(commonSuffixes);

    assertEquals(Optional.empty(), deviatedSuffixes.getRequiredSuffix());
    assertEquals(Optional.empty(), deviatedSuffixes.getRequiredNullableSuffix());
    assertEquals(Optional.empty(), deviatedSuffixes.getOptionalSuffix());
    assertEquals(
        commonSuffixes.getOptionalNullableSuffix(), deviatedSuffixes.getOptionalNullableSuffix());
  }

  @Test
  void withCommonSuffixes_when_specificOptionalNullableSuffixDefined_then_specificSuffixUsed() {
    final GetterSuffixes getterSuffixes =
        GetterSuffixes.allUndefined().withOptionalNullableSuffix("OptNull1");
    final GetterSuffixes commonSuffixes =
        GetterSuffixes.allUndefined().withOptionalNullableSuffix("OptNull2");

    final GetterSuffixes deviatedSuffixes = getterSuffixes.withCommonSuffixes(commonSuffixes);

    assertEquals(Optional.empty(), deviatedSuffixes.getRequiredSuffix());
    assertEquals(Optional.empty(), deviatedSuffixes.getRequiredNullableSuffix());
    assertEquals(Optional.empty(), deviatedSuffixes.getOptionalSuffix());
    assertEquals(
        getterSuffixes.getOptionalNullableSuffix(), deviatedSuffixes.getOptionalNullableSuffix());
  }

  @Test
  void getSuffixOrDefault_when_uninitialized_then_returnDefaultSuffixes() {
    final GetterSuffixes getterSuffixes = GetterSuffixes.allUndefined();
    assertEquals("", getterSuffixes.getRequiredSuffixOrDefault());
    assertEquals("Nullable", getterSuffixes.getRequiredNullableSuffixOrDefault());
    assertEquals("Opt", getterSuffixes.getOptionalSuffixOrDefault());
    assertEquals("OptNullable", getterSuffixes.getOptionalNullableSuffixOrDefault());
  }
}
