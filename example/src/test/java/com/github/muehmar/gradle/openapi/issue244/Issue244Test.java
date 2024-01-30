package com.github.muehmar.gradle.openapi.issue244;

import static com.github.muehmar.gradle.openapi.issue244.PatientDto.fullPatientDtoBuilder;
import static com.github.muehmar.gradle.openapi.issue244.PatientDto.patientDtoBuilder;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class Issue244Test {
  @Test
  void personDtoBuilder_when_used_then_allNestedAllOfPropertiesPresent() {
    final PatientDto patientDto =
        patientDtoBuilder()
            .setFirstName("John")
            .setLastName("Doe")
            .setBirthDate(LocalDate.of(1990, 1, 1))
            .setStreet("Wallstreet")
            .setCity("New York")
            .setPatientId("patientId")
            .andAllOptionals()
            .build();

    assertNotNull(patientDto);
  }

  @Test
  void fullPatientDtoBuilder_when_used_then_allNestedAllOfPropertiesPresent() {
    final PatientDto patientDto =
        fullPatientDtoBuilder()
            .setFirstName("John")
            .setLastName("Doe")
            .setBirthDate(LocalDate.of(1990, 1, 1))
            .setStreet("Wallstreet")
            .setCity("New York")
            .setPatientId("patientId")
            .build();

    assertNotNull(patientDto);
  }
}
