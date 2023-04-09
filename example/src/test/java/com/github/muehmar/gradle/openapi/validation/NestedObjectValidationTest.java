package com.github.muehmar.gradle.openapi.validation;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;

import OpenApiSchema.example.api.validation.model.AllValueObjectDto;
import OpenApiSchema.example.api.validation.model.NestedDto;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

class NestedObjectValidationTest {
  @Test
  void validate_when_nestedObjIsValid_then_noViolations() {
    final AllValueObjectDto obj =
        AllValueObjectDto.newBuilder().andOptionals().setIntValue(2).build();
    final NestedDto dto = NestedDto.newBuilder().setId("id").setObj(obj).build();

    final Set<ConstraintViolation<NestedDto>> constraintViolations = validate(dto);

    assertEquals(0, constraintViolations.size());
  }

  @Test
  void validate_when_nestedObjIsInvalid_then_noViolations() {
    final AllValueObjectDto obj =
        AllValueObjectDto.newBuilder().andOptionals().setIntValue(-1000).build();
    final NestedDto dto = NestedDto.newBuilder().setId("id").setObj(obj).build();

    final Set<ConstraintViolation<NestedDto>> constraintViolations = validate(dto);

    assertEquals(1, constraintViolations.size());
  }
}