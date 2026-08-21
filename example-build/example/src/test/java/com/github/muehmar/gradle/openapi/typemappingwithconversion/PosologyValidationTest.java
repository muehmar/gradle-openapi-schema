package com.github.muehmar.gradle.openapi.typemappingwithconversion;

import static com.github.muehmar.gradle.openapi.Lists.list;
import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static com.github.muehmar.gradle.openapi.util.ViolationFormatter.formatViolations;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.stream.Stream;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

public class PosologyValidationTest {
  @Test
  void validate_when_everythingOk_then_noViolations() {
    final PosologyDto posologyDto =
        PosologyDto.fromItems(
            CustomList.fromList(
                Stream.of("1", "2", "3", "0").map(CustomString::new).collect(toList())));

    final Set<ConstraintViolation<PosologyDto>> violations = validate(posologyDto);

    assertEquals(0, violations.size());
    assertTrue(posologyDto.isValid());
  }

  @Test
  void validate_when_itemsTooShort_then_violations() {
    final PosologyDto posologyDto =
        PosologyDto.fromItems(
            CustomList.fromList(
                Stream.of("1", "", "3", "").map(CustomString::new).collect(toList())));

    final Set<ConstraintViolation<PosologyDto>> violations = validate(posologyDto);

    assertEquals(
        list(
            "itemsRaw[1].<list element> -> size must be between 1 and 2147483647",
            "itemsRaw[3].<list element> -> size must be between 1 and 2147483647"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(posologyDto.isValid());
  }

  @Test
  void validate_when_tooManyItems_then_violations() {
    final PosologyDto posologyDto =
        PosologyDto.fromItems(
            CustomList.fromList(
                Stream.of("1", "2", "3", "0", "4").map(CustomString::new).collect(toList())));

    final Set<ConstraintViolation<PosologyDto>> violations = validate(posologyDto);

    assertEquals(
        list("itemsRaw -> size must be between 0 and 4"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(posologyDto.isValid());
  }
}
