package com.github.muehmar.gradle.openapi.issues.issue339;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.openapi.util.Tristate;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class Issue339Test {
  @Test
  void fromWords_when_singlePropContainer1_then_dtoCreated() {
    final SinglePropContainer1Dto container =
        SinglePropContainer1Dto.fromWords(Collections.singletonList("word1"));

    assertEquals(Collections.singletonList("word1"), container.getWords());
  }

  @Test
  void fromWords_when_singlePropContainer2_then_dtoCreated() {
    final SinglePropContainer2Dto container =
        SinglePropContainer2Dto.fromWords(Collections.singletonList("word1"));

    assertEquals(Collections.singletonList("word1"), container.getWordsOr(Collections.emptyList()));
  }

  @Test
  void fromWords_when_singlePropContainer2OptionalMethod_then_dtoCreated() {
    final SinglePropContainer2Dto container =
        SinglePropContainer2Dto.fromWords(Optional.of(Collections.singletonList("word1")));

    assertEquals(Collections.singletonList("word1"), container.getWordsOr(Collections.emptyList()));
  }

  @Test
  void fromWords_when_singlePropContainer3_then_dtoCreated() {
    final SinglePropContainer3Dto container =
        SinglePropContainer3Dto.fromWords(Collections.singletonList("word1"));

    assertEquals(Collections.singletonList("word1"), container.getWordsOr(Collections.emptyList()));
  }

  @Test
  void fromWords_when_singlePropContainer3OptionalMethod_then_dtoCreated() {
    final SinglePropContainer3Dto container =
        SinglePropContainer3Dto.fromWords(Optional.of(Collections.singletonList("word1")));

    assertEquals(Collections.singletonList("word1"), container.getWordsOr(Collections.emptyList()));
  }

  @Test
  void fromWords_when_singlePropContainer4_then_dtoCreated() {
    final SinglePropContainer4Dto container =
        SinglePropContainer4Dto.fromWords(Collections.singletonList("word1"));

    assertEquals(
        Collections.singletonList("word1"),
        container.getWordsTristate().toOptional().orElse(Collections.emptyList()));
  }

  @Test
  void fromWords_when_singlePropContainer4TristateMethod_then_dtoCreated() {
    final SinglePropContainer4Dto container =
        SinglePropContainer4Dto.fromWords(Tristate.ofValue(Collections.singletonList("word1")));

    assertEquals(
        Collections.singletonList("word1"),
        container.getWordsTristate().toOptional().orElse(Collections.emptyList()));
  }
}
