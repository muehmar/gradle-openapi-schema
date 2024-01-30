package com.github.muehmar.gradle.openapi.issue227;

import static com.github.muehmar.gradle.openapi.issue227.PatientDto.fullPatientDtoBuilder;
import static com.github.muehmar.gradle.openapi.issue227.PatientDto.patientDtoBuilder;
import static com.github.muehmar.gradle.openapi.util.MethodList.listMethodNames;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class Issue227Test {

  private static final PersonDto PERSON_DTO =
      PersonDto.personDtoBuilder()
          .setFirstName("John")
          .setLastName("Doe")
          .setBirthDate(LocalDate.of(1990, 1, 1))
          .andAllOptionals()
          .build();

  @Test
  void fullPatientDtoBuilder_when_propertyStagesUsed_then_noPropertiesTwice() {
    final PatientDto patientDto =
        fullPatientDtoBuilder()
            .setFirstName("John")
            .setLastName("Doe")
            .setBirthDate(LocalDate.of(1990, 1, 1))
            .setEmployeeId("employeeId")
            .setPatientId("patientId")
            .build();
    assertNotNull(patientDto);
  }

  @Test
  void patientDtoBuilder_when_propertyStagesUsed_then_noPropertiesTwice() {
    final PatientDto patientDto =
        patientDtoBuilder()
            .setFirstName("John")
            .setLastName("Doe")
            .setBirthDate(LocalDate.of(1990, 1, 1))
            .setEmployeeId("employeeId")
            .setPatientId("patientId")
            .andAllOptionals()
            .build();
    assertNotNull(patientDto);
  }

  @Test
  void fullPatientDtoBuilder_when_propertyStagesUsed_then_noDtoSetterInReturnedStage() {
    final Object stage =
        fullPatientDtoBuilder()
            .setFirstName("John")
            .setLastName("Doe")
            .setBirthDate(LocalDate.of(1990, 1, 1));

    assertEquals("setEmployeeId", listMethodNames(stage.getClass()));
  }

  @Test
  void patientDtoBuilder_when_propertyStagesUsed_then_noDtoSetterInReturnedStage() {
    final Object stage =
        patientDtoBuilder()
            .setFirstName("John")
            .setLastName("Doe")
            .setBirthDate(LocalDate.of(1990, 1, 1));

    assertEquals("setEmployeeId", listMethodNames(stage.getClass()));
  }

  @Test
  void fullPatientDtoBuilder_when_dtoStagesUsed_then_noPropertySetterInReturnedStage() {
    final Object stage = fullPatientDtoBuilder().setPersonDto(PERSON_DTO);

    assertEquals("setEmployeeDto", listMethodNames(stage.getClass()));
  }

  @Test
  void patientDtoBuilder_when_dtoStagesUsed_then_noPropertySetterInReturnedStage() {
    final Object stage = patientDtoBuilder().setPersonDto(PERSON_DTO);

    assertEquals("setEmployeeDto", listMethodNames(stage.getClass()));
  }
}
