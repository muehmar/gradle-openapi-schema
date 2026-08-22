package com.github.muehmar.gradle.openapi.issues.issue376;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.util.ValidationUtil;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Fractional bounds on integer properties must be rounded towards the valid range ({@code minimum:
 * 5.5} means the smallest valid integer is 6, {@code maximum: 100.5} means the largest one is 100)
 * instead of being truncated towards zero.
 */
public class Issue376Test {

  @Test
  void validate_when_intValueBelowFractionalMinimum_then_violation() {
    // minimum is 5.5, so 5 is invalid
    final FractionalBoundsDto dto =
        FractionalBoundsDto.fullBuilder().setIntValue(Optional.of(5)).build();

    assertFalse(
        ValidationUtil.validate(dto).isEmpty(), "Expected a violation for intValue 5 < 5.5");
  }

  @Test
  void validate_when_intValueAtRoundedUpFractionalMinimum_then_noViolation() {
    // minimum is 5.5, so 6 is the smallest valid value
    final FractionalBoundsDto dto =
        FractionalBoundsDto.fullBuilder().setIntValue(Optional.of(6)).build();

    assertTrue(ValidationUtil.validate(dto).isEmpty(), "Expected no violation for intValue 6");
  }

  @Test
  void validate_when_intValueAtRoundedDownFractionalMaximum_then_noViolation() {
    // maximum is 100.5, so 100 is still valid
    final FractionalBoundsDto dto =
        FractionalBoundsDto.fullBuilder().setIntValue(Optional.of(100)).build();

    assertTrue(ValidationUtil.validate(dto).isEmpty(), "Expected no violation for intValue 100");
  }

  @Test
  void validate_when_intValueAboveFractionalMaximum_then_violation() {
    // maximum is 100.5, so 101 is invalid
    final FractionalBoundsDto dto =
        FractionalBoundsDto.fullBuilder().setIntValue(Optional.of(101)).build();

    assertFalse(
        ValidationUtil.validate(dto).isEmpty(), "Expected a violation for intValue 101 > 100.5");
  }

  @Test
  void validate_when_exclusiveFractionalBoundsAtValidEdges_then_noViolation() {
    // exclusiveMinimum 5.5 and exclusiveMaximum 100.5 still admit 6 and 100
    assertTrue(
        ValidationUtil.validate(
                FractionalExclusiveBoundsDto.fullBuilder().setIntValue(Optional.of(6)).build())
            .isEmpty(),
        "Expected no violation for intValue 6");
    assertTrue(
        ValidationUtil.validate(
                FractionalExclusiveBoundsDto.fullBuilder().setIntValue(Optional.of(100)).build())
            .isEmpty(),
        "Expected no violation for intValue 100");
  }

  @Test
  void validate_when_exclusiveFractionalBoundsOutsideValidRange_then_violation() {
    assertFalse(
        ValidationUtil.validate(
                FractionalExclusiveBoundsDto.fullBuilder().setIntValue(Optional.of(5)).build())
            .isEmpty(),
        "Expected a violation for intValue 5");
    assertFalse(
        ValidationUtil.validate(
                FractionalExclusiveBoundsDto.fullBuilder().setIntValue(Optional.of(101)).build())
            .isEmpty(),
        "Expected a violation for intValue 101");
  }

  @Test
  void validate_when_negativeFractionalBoundsAtValidEdges_then_noViolation() {
    // minimum -100.5 rounds up to -100, maximum -5.5 rounds down to -6
    assertTrue(
        ValidationUtil.validate(
                NegativeFractionalBoundsDto.fullBuilder().setIntValue(Optional.of(-100)).build())
            .isEmpty(),
        "Expected no violation for intValue -100");
    assertTrue(
        ValidationUtil.validate(
                NegativeFractionalBoundsDto.fullBuilder().setIntValue(Optional.of(-6)).build())
            .isEmpty(),
        "Expected no violation for intValue -6");
  }

  @Test
  void validate_when_negativeFractionalBoundsOutsideValidRange_then_violation() {
    assertFalse(
        ValidationUtil.validate(
                NegativeFractionalBoundsDto.fullBuilder().setIntValue(Optional.of(-101)).build())
            .isEmpty(),
        "Expected a violation for intValue -101 < -100.5");
    assertFalse(
        ValidationUtil.validate(
                NegativeFractionalBoundsDto.fullBuilder().setIntValue(Optional.of(-5)).build())
            .isEmpty(),
        "Expected a violation for intValue -5 > -5.5");
  }

  @Test
  void validate_when_integralBoundsAtValidEdges_then_noViolation() {
    // integral bounds must keep behaving as before: inclusive 5..100, exclusive 6..99
    assertTrue(
        ValidationUtil.validate(
                IntegralBoundsDto.fullBuilder()
                    .setInclusiveValue(Optional.of(5))
                    .setExclusiveValue(Optional.of(6))
                    .build())
            .isEmpty(),
        "Expected no violation for the lower edges");
    assertTrue(
        ValidationUtil.validate(
                IntegralBoundsDto.fullBuilder()
                    .setInclusiveValue(Optional.of(100))
                    .setExclusiveValue(Optional.of(99))
                    .build())
            .isEmpty(),
        "Expected no violation for the upper edges");
  }

  @Test
  void validate_when_integralExclusiveBoundsAtExcludedValues_then_violation() {
    assertFalse(
        ValidationUtil.validate(
                IntegralBoundsDto.fullBuilder()
                    .setInclusiveValue(Optional.of(5))
                    .setExclusiveValue(Optional.of(5))
                    .build())
            .isEmpty(),
        "Expected a violation for exclusiveValue 5");
    assertFalse(
        ValidationUtil.validate(
                IntegralBoundsDto.fullBuilder()
                    .setInclusiveValue(Optional.of(100))
                    .setExclusiveValue(Optional.of(100))
                    .build())
            .isEmpty(),
        "Expected a violation for exclusiveValue 100");
  }

  @Test
  void validate_when_justInsideFractionalBounds_then_noViolation() {
    // 7 and 99 are comfortably inside 5.5..100.5, guarding against an
    // off-by-one that would shift the whole valid range
    assertTrue(
        ValidationUtil.validate(
                FractionalBoundsDto.fullBuilder().setIntValue(Optional.of(7)).build())
            .isEmpty(),
        "Expected no violation for intValue 7");
    assertTrue(
        ValidationUtil.validate(
                FractionalBoundsDto.fullBuilder().setIntValue(Optional.of(99)).build())
            .isEmpty(),
        "Expected no violation for intValue 99");
  }

  @Test
  void validate_when_wellOutsideFractionalBounds_then_violation() {
    assertFalse(
        ValidationUtil.validate(
                FractionalBoundsDto.fullBuilder().setIntValue(Optional.of(0)).build())
            .isEmpty(),
        "Expected a violation for intValue 0");
    assertFalse(
        ValidationUtil.validate(
                FractionalBoundsDto.fullBuilder().setIntValue(Optional.of(1000)).build())
            .isEmpty(),
        "Expected a violation for intValue 1000");
  }

  @Test
  void validate_when_smallFractionMinimumAtBoundary_then_roundedAwayFromInvalidRange() {
    // minimum is 5.1: rounding towards the valid range yields 6, so 5 stays
    // invalid even though 5 is the nearest integer
    assertFalse(
        ValidationUtil.validate(
                SmallFractionBoundsDto.fullBuilder().setIntValue(Optional.of(5)).build())
            .isEmpty(),
        "Expected a violation for intValue 5 < 5.1");
    assertTrue(
        ValidationUtil.validate(
                SmallFractionBoundsDto.fullBuilder().setIntValue(Optional.of(6)).build())
            .isEmpty(),
        "Expected no violation for intValue 6");
  }

  @Test
  void validate_when_largeFractionMaximumAtBoundary_then_roundedAwayFromInvalidRange() {
    // maximum is 100.9: rounding towards the valid range yields 100, so 101
    // stays invalid even though 101 is the nearest integer
    assertTrue(
        ValidationUtil.validate(
                SmallFractionBoundsDto.fullBuilder().setIntValue(Optional.of(100)).build())
            .isEmpty(),
        "Expected no violation for intValue 100");
    assertFalse(
        ValidationUtil.validate(
                SmallFractionBoundsDto.fullBuilder().setIntValue(Optional.of(101)).build())
            .isEmpty(),
        "Expected a violation for intValue 101 > 100.9");
  }

  @Test
  void validate_when_negativeFractionalBoundsJustInside_then_noViolation() {
    // -99 and -7 are inside -100.5..-5.5
    assertTrue(
        ValidationUtil.validate(
                NegativeFractionalBoundsDto.fullBuilder().setIntValue(Optional.of(-99)).build())
            .isEmpty(),
        "Expected no violation for intValue -99");
    assertTrue(
        ValidationUtil.validate(
                NegativeFractionalBoundsDto.fullBuilder().setIntValue(Optional.of(-7)).build())
            .isEmpty(),
        "Expected no violation for intValue -7");
  }

  @Test
  void validate_when_zeroAgainstNegativeFractionalBounds_then_violation() {
    // 0 is above the maximum of -5.5
    assertFalse(
        ValidationUtil.validate(
                NegativeFractionalBoundsDto.fullBuilder().setIntValue(Optional.of(0)).build())
            .isEmpty(),
        "Expected a violation for intValue 0 > -5.5");
  }

  @Test
  void validate_when_exclusiveFractionalBoundsJustInside_then_noViolation() {
    assertTrue(
        ValidationUtil.validate(
                FractionalExclusiveBoundsDto.fullBuilder().setIntValue(Optional.of(7)).build())
            .isEmpty(),
        "Expected no violation for intValue 7");
    assertTrue(
        ValidationUtil.validate(
                FractionalExclusiveBoundsDto.fullBuilder().setIntValue(Optional.of(99)).build())
            .isEmpty(),
        "Expected no violation for intValue 99");
  }

  @Test
  void validate_when_integralExclusiveBoundsJustInsideExcludedValues_then_noViolation() {
    // exclusive 5..100 admits 6..99, so both edges of that range are valid
    assertTrue(
        ValidationUtil.validate(
                IntegralBoundsDto.fullBuilder()
                    .setInclusiveValue(Optional.of(5))
                    .setExclusiveValue(Optional.of(6))
                    .build())
            .isEmpty(),
        "Expected no violation for exclusiveValue 6");
    assertTrue(
        ValidationUtil.validate(
                IntegralBoundsDto.fullBuilder()
                    .setInclusiveValue(Optional.of(100))
                    .setExclusiveValue(Optional.of(99))
                    .build())
            .isEmpty(),
        "Expected no violation for exclusiveValue 99");
  }

  @Test
  void validate_when_integralInclusiveBoundsOutsideRange_then_violation() {
    // inclusive 5..100 rejects 4 and 101, unchanged by the rounding fix
    assertFalse(
        ValidationUtil.validate(
                IntegralBoundsDto.fullBuilder()
                    .setInclusiveValue(Optional.of(4))
                    .setExclusiveValue(Optional.of(50))
                    .build())
            .isEmpty(),
        "Expected a violation for inclusiveValue 4");
    assertFalse(
        ValidationUtil.validate(
                IntegralBoundsDto.fullBuilder()
                    .setInclusiveValue(Optional.of(101))
                    .setExclusiveValue(Optional.of(50))
                    .build())
            .isEmpty(),
        "Expected a violation for inclusiveValue 101");
  }
}
