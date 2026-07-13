package com.github.muehmar.gradle.openapi.issues.issue376;

import static org.junit.jupiter.api.Assertions.assertFalse;

import com.github.muehmar.gradle.openapi.util.ValidationUtil;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Fractional bounds on integer properties must be rounded towards the valid range ({@code minimum:
 * 5.5} means the smallest valid integer is 6). The constraint mapper truncates with {@code
 * BigDecimal.longValue()}, generating {@code @Min(5)}, so the invalid value 5 passes validation.
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
}
