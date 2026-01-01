package com.github.muehmar.gradle.openapi.typemappingwithconversion;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import org.junit.jupiter.api.Test;

public class PatientDeserialisationTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void writeValueAsString_whenPatient_then_matchJson() throws Exception {

    final PatientDto patientDto =
        MAPPER.readValue(
            "{\"address\":{\"city\":\"Winterthur\",\"number\":5,\"street\":\"Vogelsang\",\"zipCode\":8404},\"fhirData\":{\"data1\":\"fhirData\",\"data2\":500},\"id\":1234}",
            PatientDto.class);

    final CustomAddress customAddress = new CustomAddress("Vogelsang", 5, 8404, "Winterthur");
    final FhirData fhirData = new FhirData("fhirData", 500);
    final PatientDto expectedPatientDto =
        PatientDto.fullPatientDtoBuilder()
            .setId(1234)
            .setAddress(customAddress)
            .setFhirData(fhirData)
            .build();

    assertEquals(expectedPatientDto, patientDto);
  }
}
