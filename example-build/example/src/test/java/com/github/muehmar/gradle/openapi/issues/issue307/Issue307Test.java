package com.github.muehmar.gradle.openapi.issues.issue307;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.typemappingwithconversion.CustomString;
import com.github.muehmar.openapi.util.AdditionalProperty;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class Issue307Test {
  @Test
  void
      mapAdditionalPropertiesPropertyDto_when_additionalPropertyUsed_then_hasCustomStringAsApiType() {
    final MapAdditionalPropertiesPropertyDto data =
        MapAdditionalPropertiesPropertyDto.fullBuilder()
            .addAdditionalProperty("prop1", CustomString.fromString("value1"))
            .build();

    final MapAdditionalPropertiesDto dto =
        MapAdditionalPropertiesDto.fullBuilder()
            .setName(CustomString.fromString("name"))
            .addAdditionalProperty("data", data)
            .build();

    final List<AdditionalProperty<CustomString>> additionalProperties =
        dto.getAdditionalProperty("data")
            .map(MapAdditionalPropertiesPropertyDto::getAdditionalProperties)
            .orElse(Collections.emptyList());

    assertEquals(
        additionalProperties,
        Collections.singletonList(
            new AdditionalProperty<>("prop1", CustomString.fromString("value1"))));
  }
}
