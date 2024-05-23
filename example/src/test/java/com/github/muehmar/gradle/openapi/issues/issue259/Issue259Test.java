package com.github.muehmar.gradle.openapi.issues.issue259;

import static com.github.muehmar.gradle.openapi.issues.issue259.ArticleDto.fullArticleDtoBuilder;
import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static com.github.muehmar.gradle.openapi.util.ViolationFormatter.formatViolations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

public class Issue259Test {
  @Test
  void fullArticleDtoBuilder_when_validGtin_then_noViolations() {
    final ArticleDto dto = fullArticleDtoBuilder().setGtin(9876543210987L).build();

    final Set<ConstraintViolation<ArticleDto>> violations = validate(dto);

    assertEquals(0, violations.size());
    assertTrue(dto.isValid());
  }

  @Test
  void fullArticleDtoBuilder_when_invalidGtin_then_violations() {
    final ArticleDto dto = fullArticleDtoBuilder().setGtin(0L).build();

    final Set<ConstraintViolation<ArticleDto>> violations = validate(dto);

    assertEquals(
        Collections.singletonList("gtin -> must be greater than or equal to 1000000000000"),
        formatViolations(violations));
    assertFalse(dto.isValid());
  }
}
