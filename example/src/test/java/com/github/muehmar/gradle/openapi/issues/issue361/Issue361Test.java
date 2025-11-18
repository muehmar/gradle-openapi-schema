package com.github.muehmar.gradle.openapi.issues.issue361;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;

class Issue361Test {
  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void uniqueTags_when_deserializedFromJsonWithDuplicates_then_noValidationError()
      throws Exception {
    final String json =
        "{"
            + "\"tags\": [\"tag1\", \"tag2\"],"
            + "\"uniqueTags\": [\"duplicate\", \"duplicate\", \"value\"]"
            + "}";

    final TagCollectionDto dto = objectMapper.readValue(json, TagCollectionDto.class);

    assertEquals(2, dto.getUniqueTags().size(), "Public API returns Set with duplicates removed");

    final Set<ConstraintViolation<TagCollectionDto>> violations = validator.validate(dto);

    assertTrue(
        violations.isEmpty(),
        "Expected no validation errors for JSON with duplicates when disableUniqueItemsValidation=true, "
            + "but got: "
            + violations);
    assertTrue(dto.isValid());
  }

  @Test
  void uniqueNumbers_when_deserializedFromJsonWithDuplicates_then_noValidationError()
      throws Exception {
    final String json =
        "{"
            + "\"tags\": [\"tag1\"],"
            + "\"uniqueTags\": [\"a\", \"b\"],"
            + "\"uniqueNumbers\": [1, 2, 2, 3, 3, 3]"
            + "}";

    final TagCollectionDto dto = objectMapper.readValue(json, TagCollectionDto.class);

    final Set<ConstraintViolation<TagCollectionDto>> violations = validator.validate(dto);

    assertTrue(
        violations.isEmpty(),
        "Expected no validation errors for JSON with duplicate numbers when disableUniqueItemsValidation=true, "
            + "but got: "
            + violations);
    assertTrue(dto.isValid());
  }

  @Test
  void optionalUniqueTags_when_deserializedFromJsonWithDuplicates_then_noValidationError()
      throws Exception {
    final String json =
        "{"
            + "\"tags\": [\"tag1\"],"
            + "\"uniqueTags\": [\"a\", \"b\"],"
            + "\"optionalUniqueTags\": [\"same\", \"same\", \"same\"]"
            + "}";

    final TagCollectionDto dto = objectMapper.readValue(json, TagCollectionDto.class);

    final Set<ConstraintViolation<TagCollectionDto>> violations = validator.validate(dto);

    assertTrue(
        violations.isEmpty(),
        "Expected no validation errors for JSON with duplicates in optional array when disableUniqueItemsValidation=true, "
            + "but got: "
            + violations);
    assertTrue(dto.isValid());
  }

  @Test
  void allArrays_when_deserializedFromJsonWithDuplicates_then_noValidationError() throws Exception {
    final String json =
        "{"
            + "\"tags\": [\"regular\", \"regular\", \"allowed\"],"
            + "\"uniqueTags\": [\"dup\", \"dup\"],"
            + "\"uniqueNumbers\": [5, 5, 5],"
            + "\"optionalUniqueTags\": [\"x\", \"x\"]"
            + "}";

    final TagCollectionDto dto = objectMapper.readValue(json, TagCollectionDto.class);

    final Set<ConstraintViolation<TagCollectionDto>> violations = validator.validate(dto);

    assertTrue(
        violations.isEmpty(),
        "Expected no validation errors when all arrays have duplicates and disableUniqueItemsValidation=true, "
            + "but got: "
            + violations);
    assertTrue(dto.isValid());
  }
}
