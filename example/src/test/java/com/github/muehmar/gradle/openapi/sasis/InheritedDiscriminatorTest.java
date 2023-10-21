package com.github.muehmar.gradle.openapi.sasis;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

class InheritedDiscriminatorTest {
  @Test
  void dtoHasDiscriminatorValidationMethodWhichMeansTheDiscriminatorInTheParentSchemasWasFound() {
    final Class<ResponsesContractCareProviderPartyPartyDto> dtoClass =
        ResponsesContractCareProviderPartyPartyDto.class;

    final boolean containsDiscriminatorValidationMethod =
        Arrays.stream(dtoClass.getDeclaredMethods())
            .anyMatch(method -> method.getName().equals("isValidAgainstTheCorrectSchema"));

    assertTrue(containsDiscriminatorValidationMethod);
  }
}
