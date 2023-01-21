package com.github.muehmar.jakarta3;

import static org.junit.jupiter.api.Assertions.assertEquals;

import OpenApiSchema.examplejakarta3.api.model.PatientDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingConsumer;

class PatientTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Test
  void validate_when_ok_then_noValidationError() throws Throwable {
    runValidation(
        validator -> {
          PatientDto dto =
              PatientDto.newBuilder()
                  .setId("123")
                  .setName("Dexter")
                  .andOptionals()
                  .setSurname("morgan")
                  .build();

          final Set<ConstraintViolation<PatientDto>> violations = validator.validate(dto);

          assertEquals(0, violations.size());
        });
  }

  @Test
  void validate_when_propertyCountTooHigh_then_validationError() throws Throwable {
    runValidation(
        validator -> {
          PatientDto dto =
              PatientDto.newBuilder()
                  .setId("123")
                  .setName("Dexter")
                  .andAllOptionals()
                  .setSurname("morgan")
                  .setAge(40)
                  .build();

          final Set<ConstraintViolation<PatientDto>> violations = validator.validate(dto);

          assertEquals(1, violations.size());
        });
  }

  @Test
  void validate_when_ageTooLow_then_validationError() throws Throwable {
    runValidation(
        validator -> {
          PatientDto dto =
              PatientDto.newBuilder()
                  .setId("123")
                  .setName("Dexter")
                  .andOptionals()
                  .setAge(5)
                  .build();

          final Set<ConstraintViolation<PatientDto>> violations = validator.validate(dto);

          assertEquals(1, violations.size());
        });
  }

  @Test
  void validate_when_surnameDoesNotMatchPattern_then_validationError() throws Throwable {
    runValidation(
        validator -> {
          PatientDto dto =
              PatientDto.newBuilder()
                  .setId("123")
                  .setName("Dexter")
                  .andOptionals()
                  .setSurname("123")
                  .build();

          final Set<ConstraintViolation<PatientDto>> violations = validator.validate(dto);

          assertEquals(1, violations.size());
        });
  }

  @Test
  void validate_when_requiredNameMissing_then_validationError() throws Throwable {
    runValidation(
        validator -> {
          final PatientDto dto =
              MAPPER.readValue(
                  "{\"id\":\"123\",\"surname\":\"morgan\",\"age\":40}", PatientDto.class);

          final Set<ConstraintViolation<PatientDto>> violations = validator.validate(dto);

          assertEquals(1, violations.size());
        });
  }

  private static void runValidation(ThrowingConsumer<Validator> run) throws Throwable {
    try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
      Validator validator = factory.getValidator();
      run.accept(validator);
    }
  }
}
