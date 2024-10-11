package com.github.muehmar.gradle.openapi.typemappingwithconversion;

import static com.github.muehmar.gradle.openapi.Lists.list;
import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static com.github.muehmar.gradle.openapi.util.ViolationFormatter.formatViolations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

public class PatientValidationTest {

  @Test
  void validate_when_everythingOk_then_noViolations() {
    final CustomAddress customAddress = new CustomAddress("Vogelsang", 5, 8404, "Winterthur");
    final FhirData fhirData = new FhirData("fhirData", 500);
    final PatientDto patientDto =
        PatientDto.fullPatientDtoBuilder()
            .setId(1234)
            .setAddress(customAddress)
            .setFhirData(fhirData)
            .build();

    final Set<ConstraintViolation<PatientDto>> violations = validate(patientDto);

    assertEquals(0, violations.size());
    assertTrue(patientDto.isValid());
  }

  @Test
  void validate_when_streetTooLongAndZipCodeTooLow_then_violations() {
    final CustomAddress customAddress =
        new CustomAddress("Vogelsang-Vogelsang-Vogelsang", 5, 99, "Winterthur");
    final FhirData fhirData = new FhirData("fhirData", 500);
    final PatientDto patientDto =
        PatientDto.fullPatientDtoBuilder()
            .setId(1234)
            .setAddress(customAddress)
            .setFhirData(fhirData)
            .build();

    final Set<ConstraintViolation<PatientDto>> violations = validate(patientDto);

    assertEquals(
        list(
            "addressRaw.streetRaw -> size must be between 0 and 15",
            "addressRaw.zipCode -> must be greater than or equal to 1000"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(patientDto.isValid());
  }
}
