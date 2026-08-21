package com.github.muehmar.gradle.openapi.util;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;

public class ViolationFormatter {
  private ViolationFormatter() {}

  public static <T> List<String> formatViolations(Set<ConstraintViolation<T>> violations) {
    return violations.stream()
        .map(
            violation ->
                String.format("%s -> %s", violation.getPropertyPath(), violation.getMessage()))
        .sorted()
        .collect(Collectors.toList());
  }
}
