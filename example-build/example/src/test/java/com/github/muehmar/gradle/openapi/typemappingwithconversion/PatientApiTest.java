package com.github.muehmar.gradle.openapi.typemappingwithconversion;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class PatientApiTest {
  @Test
  void stagedBuilder_when_standardSetterUsed_then_getterReturnsCorrectValue() {
    final CustomAddress customAddress = new CustomAddress("Vogelsang", 5, 8404, "Winterthur");
    final FhirData fhirData = new FhirData("fhirData", 500);
    final PatientDto patientDto =
        PatientDto.fullPatientDtoBuilder()
            .setId(1234)
            .setAddress(customAddress)
            .setFhirData(fhirData)
            .build();

    assertEquals(customAddress, patientDto.getAddress());
  }

  @Test
  void withers_when_used_propertySetCorrectly() {
    final CustomAddress customAddress1 = new CustomAddress("Vogelsang", 5, 8404, "Winterthur");
    final CustomAddress customAddress2 =
        new CustomAddress("Pflanzschulstrasse", 20, 8000, "Zürich");
    final FhirData fhirData1 = new FhirData("fhirData1", 500);
    final FhirData fhirData2 = new FhirData("fhirData2", 1000);
    final PatientDto patientDto =
        PatientDto.fullPatientDtoBuilder()
            .setId(1234)
            .setAddress(customAddress1)
            .setFhirData(fhirData1)
            .build();

    final PatientDto adjustedPatient =
        patientDto.withAddress(customAddress2).withFhirData(fhirData2);

    assertEquals(customAddress2, adjustedPatient.getAddress());
    assertEquals(fhirData2, adjustedPatient.getFhirData());
  }
}
