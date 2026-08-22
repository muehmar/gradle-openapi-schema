package com.github.muehmar.gradle.openapi.issues.issue377;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.util.ValidationUtil;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * {@code minLength}/{@code maxLength} on a {@code type: string, format: binary} property maps to a
 * {@code byte[]} member. Bean Validation supports {@code @Size} on arrays including {@code byte[]},
 * so the size constraint must be rendered as a {@code @Size} annotation instead of being reported
 * as unsupported. The annotation and the generated {@code isValid()} deep validation must agree on
 * the bounds: before the fix only the latter enforced them.
 */
public class Issue377Test {

  @ParameterizedTest
  @ValueSource(ints = {2, 3, 4})
  void validate_when_binaryLengthWithinBounds_then_noViolation(int length) {
    // minLength 2, maxLength 4, hence the bounds themselves are valid too
    final BinaryHolderDto dto = binaryHolder(length);

    assertTrue(
        ValidationUtil.validate(dto).isEmpty(), "Expected no violation for " + length + " bytes");
    assertTrue(dto.isValid(), "Expected isValid() for " + length + " bytes");
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 5, 10})
  void validate_when_binaryLengthOutsideBounds_then_violation(int length) {
    // minLength 2, maxLength 4, hence 1 and 5 are the values just outside the bounds
    final BinaryHolderDto dto = binaryHolder(length);

    assertEquals(
        1, ValidationUtil.validate(dto).size(), "Expected a violation for " + length + " bytes");
    assertFalse(dto.isValid(), "Expected not isValid() for " + length + " bytes");
  }

  @Test
  void validate_when_minLengthOnlyAtAndBelowBound_then_violationOnlyBelow() {
    // no maxLength, so an arbitrarily long value stays valid
    assertTrue(
        ValidationUtil.validate(minLengthOnly(2)).isEmpty(), "Expected no violation for 2 bytes");
    assertTrue(
        ValidationUtil.validate(minLengthOnly(100)).isEmpty(),
        "Expected no violation for 100 bytes without a maxLength");
    assertFalse(
        ValidationUtil.validate(minLengthOnly(1)).isEmpty(),
        "Expected a violation for 1 byte < minLength 2");
  }

  @Test
  void validate_when_maxLengthOnlyAtAndAboveBound_then_violationOnlyAbove() {
    // no minLength, so an empty value stays valid
    assertTrue(
        ValidationUtil.validate(maxLengthOnly(0)).isEmpty(),
        "Expected no violation for 0 bytes without a minLength");
    assertTrue(
        ValidationUtil.validate(maxLengthOnly(4)).isEmpty(), "Expected no violation for 4 bytes");
    assertFalse(
        ValidationUtil.validate(maxLengthOnly(5)).isEmpty(),
        "Expected a violation for 5 bytes > maxLength 4");
  }

  @Test
  void validate_when_absentOptionalBinary_then_noViolation() {
    // an absent optional property must not be size-validated at all
    final BinaryHolderDto dto = BinaryHolderDto.fullBuilder().setData(Optional.empty()).build();

    assertTrue(ValidationUtil.validate(dto).isEmpty(), "Expected no violation for absent data");
    assertTrue(dto.isValid(), "Expected isValid() for absent data");
  }

  @Test
  void validate_when_requiredBinaryOutsideBounds_then_violation() {
    // the size constraint applies to a required property the same way
    final RequiredBinaryHolderDto valid =
        RequiredBinaryHolderDto.fullBuilder().setData(bytes(3)).build();
    final RequiredBinaryHolderDto tooLong =
        RequiredBinaryHolderDto.fullBuilder().setData(bytes(5)).build();

    assertTrue(ValidationUtil.validate(valid).isEmpty(), "Expected no violation for 3 bytes");
    assertFalse(
        ValidationUtil.validate(tooLong).isEmpty(),
        "Expected a violation for 5 bytes > maxLength 4");
  }

  private static BinaryHolderDto binaryHolder(int length) {
    return BinaryHolderDto.fullBuilder().setData(Optional.of(bytes(length))).build();
  }

  private static MinLengthOnlyBinaryHolderDto minLengthOnly(int length) {
    return MinLengthOnlyBinaryHolderDto.fullBuilder().setData(Optional.of(bytes(length))).build();
  }

  private static MaxLengthOnlyBinaryHolderDto maxLengthOnly(int length) {
    return MaxLengthOnlyBinaryHolderDto.fullBuilder().setData(Optional.of(bytes(length))).build();
  }

  private static byte[] bytes(int length) {
    final byte[] bytes = new byte[length];
    for (int i = 0; i < length; i++) {
      bytes[i] = (byte) i;
    }
    return bytes;
  }
}
