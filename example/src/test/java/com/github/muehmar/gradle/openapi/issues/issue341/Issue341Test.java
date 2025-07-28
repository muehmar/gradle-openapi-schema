package com.github.muehmar.gradle.openapi.issues.issue341;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class Issue341Test {

  @Test
  void patientReferenceBuilder_when_used_then_samePropertiesAsPatient() {
    final PatientReferenceDto patientReferenceDto =
        PatientReferenceDto.fullBuilder()
            .setName("John Doe")
            .setGender(GenderDto.MALE)
            .setBirthdate(LocalDate.of(1980, 1, 1))
            .build();

    final PatientDto patientDto =
        PatientDto.fullBuilder()
            .setName("John Doe")
            .setGender(GenderDto.MALE)
            .setBirthdate(LocalDate.of(1980, 1, 1))
            .build();

    assertEquals(patientReferenceDto.getName(), patientDto.getName());
    assertEquals(patientReferenceDto.getGender(), patientDto.getGender());
    assertEquals(patientReferenceDto.getBirthdateOpt(), patientDto.getBirthdateOpt());
  }
}
