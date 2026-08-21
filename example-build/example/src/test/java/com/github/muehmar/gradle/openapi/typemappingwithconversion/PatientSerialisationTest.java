package com.github.muehmar.gradle.openapi.typemappingwithconversion;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import org.junit.jupiter.api.Test;

public class PatientSerialisationTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void writeValueAsString_whenPatient_then_matchJson() throws Exception {
    final CustomAddress customAddress = new CustomAddress("Vogelsang", 5, 8404, "Winterthur");
    final FhirData fhirData = new FhirData("fhirData", 500);
    final PatientDto patientDto =
        PatientDto.fullPatientDtoBuilder()
            .setId(1234)
            .setAddress(customAddress)
            .setFhirData(fhirData)
            .build();

    final String json = MAPPER.writeValueAsString(patientDto);
    assertEquals(
        "{\"address\":{\"city\":\"Winterthur\",\"number\":5,\"street\":\"Vogelsang\",\"zipCode\":8404},\"fhirData\":{\"data1\":\"fhirData\",\"data2\":500},\"id\":1234}",
        json);
  }
}
