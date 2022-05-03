package com.github.muehmar.gradle.openapi.util;

import static com.github.muehmar.gradle.openapi.util.Functions.allExceptFirst;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import org.junit.jupiter.api.Test;

class FunctionsTest {
  @Test
  void allExceptFirst_when_calledForIntegers_then_mappingAppliedToAllExceptFirstElement() {
    final PList<Integer> list = PList.of(1, 2, 3, 4, 5);

    final PList<Integer> result = list.zipWithIndex().map(allExceptFirst(i -> i + 10));

    assertEquals(PList.of(1, 12, 13, 14, 15), result);
  }
}
