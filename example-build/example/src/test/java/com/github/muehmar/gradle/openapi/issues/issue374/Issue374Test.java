package com.github.muehmar.gradle.openapi.issues.issue374;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.util.ValidationUtil;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

/**
 * A DTO with an optional array property with {@code uniqueItems: true} must be validatable when the
 * property is absent. The generated {@code @AssertTrue} method {@code hasTagsUniqueItems()} has no
 * null guard ({@code new HashSet<>(tags)} NPEs when tags is null), so bean validation throws a
 * {@code ValidationException} instead of returning violations.
 */
public class Issue374Test {

  @Test
  void validate_when_optionalUniqueItemsArrayAbsent_then_noViolations() {
    final UniqueItemsHolderDto dto =
        UniqueItemsHolderDto.fullBuilder().setId("id-1").setTags(Optional.empty()).build();

    final Set<ConstraintViolation<UniqueItemsHolderDto>> violations =
        assertDoesNotThrow(() -> ValidationUtil.validate(dto));

    assertTrue(violations.isEmpty());
  }
}
