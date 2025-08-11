package com.github.muehmar.gradle.openapi.issues.issue343;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class Issue343Test {

  @Test
  void fullBuilder_when_myGroupDto_then_containsCorrectProperties() {
    final LocalDate date = LocalDate.now();
    final MyGroupDto groupDto = MyGroupDto.fullBuilder().setDateFrom(date).build();
    assertEquals(Optional.of(date), groupDto.getDateFromOpt());
  }
}
