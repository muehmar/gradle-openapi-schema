package com.github.muehmar.gradle.openapi.issues.issue248;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.openapi.util.Tristate;
import org.junit.jupiter.api.Test;

public class Issue248Test {
  @Test
  void fullPersonDtoBuilder_when_used_then_firstNameIsTristate() {
    final PersonDto personDto =
        PersonDto.fullPersonDtoBuilder()
            .setFirstName(Tristate.ofValue("John"))
            .setLastName(Tristate.ofValue("Doe"))
            .build();

    assertEquals(Tristate.ofValue("John"), personDto.getFirstNameTristate());
    assertEquals(Tristate.ofValue("Doe"), personDto.getLastNameTristate());
  }
}
