package com.github.muehmar.jakarta3;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import openapischema.examplejakarta3.api.model.PatientDto;
import org.junit.jupiter.api.Test;

class PatientTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Test
  void validate_when_ok_then_noValidationError() {
    PatientDto dto =
        PatientDto.builder()
            .setId("123")
            .setName("Dexter")
            .andOptionals()
            .setSurname("morgan")
            .build();

    final Set<ConstraintViolation<PatientDto>> violations = validate(dto);

    assertEquals(0, violations.size());
  }

  @Test
  void validate_when_propertyCountTooHigh_then_validationError() {
    PatientDto dto =
        PatientDto.builder()
            .setId("123")
            .setName("Dexter")
            .andAllOptionals()
            .setSurname("morgan")
            .setAge(40)
            .setGender(PatientDto.GenderEnum.OTHER)
            .build();

    final Set<ConstraintViolation<PatientDto>> violations = validate(dto);

    assertEquals(1, violations.size());
  }

  @Test
  void validate_when_ageTooLow_then_validationError() {
    PatientDto dto =
        PatientDto.builder().setId("123").setName("Dexter").andOptionals().setAge(5).build();

    final Set<ConstraintViolation<PatientDto>> violations = validate(dto);

    assertEquals(1, violations.size());
  }

  @Test
  void validate_when_surnameDoesNotMatchPattern_then_validationError() {
    PatientDto dto =
        PatientDto.builder()
            .setId("123")
            .setName("Dexter")
            .andOptionals()
            .setSurname("123")
            .build();

    final Set<ConstraintViolation<PatientDto>> violations = validate(dto);

    assertEquals(1, violations.size());
  }

  @Test
  void validate_when_requiredNameMissing_then_validationError() throws Throwable {
    final PatientDto dto =
        MAPPER.readValue("{\"id\":\"123\",\"surname\":\"morgan\",\"age\":40}", PatientDto.class);

    final Set<ConstraintViolation<PatientDto>> violations = validate(dto);

    assertEquals(1, violations.size());
  }

  @Test
  void validate_when_enumValueValid_then_noValidationError() throws Throwable {
    // Issue 266: an enum is represented internally as a String and validated against a pattern
    // constraint of its members, which has to be rendered with the jakarta annotations as well.
    final PatientDto dto =
        MAPPER.readValue(
            "{\"id\":\"123\",\"name\":\"Dexter\",\"gender\":\"male\"}", PatientDto.class);

    assertEquals(PatientDto.GenderEnum.MALE, dto.getGenderOpt().orElse(null));
    assertEquals(0, validate(dto).size());
  }

  @Test
  void validate_when_enumValueOutOfRange_then_validationError() throws Throwable {
    // The out-of-range value deserializes without throwing and is reported as a violation.
    final PatientDto dto =
        MAPPER.readValue(
            "{\"id\":\"123\",\"name\":\"Dexter\",\"gender\":\"diverse\"}", PatientDto.class);

    final Set<ConstraintViolation<PatientDto>> violations = validate(dto);

    assertEquals(1, violations.size());
    assertEquals("must match \"male|female|other\"", violations.iterator().next().getMessage());
  }

  private static <T> Set<ConstraintViolation<T>> validate(T object) {
    try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
      Validator validator = factory.getValidator();
      return validator.validate(object);
    }
  }
}
