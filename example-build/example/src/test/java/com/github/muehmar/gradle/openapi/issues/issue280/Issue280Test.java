package com.github.muehmar.gradle.openapi.issues.issue280;

import static com.github.muehmar.gradle.openapi.issues.issue280.InlineEnumArrayDto.DataEnum.HELLO;
import static com.github.muehmar.gradle.openapi.issues.issue280.InlineEnumArrayDto.DataEnum.WORLD;
import static com.github.muehmar.gradle.openapi.issues.issue280.InlineEnumArrayDto.fullInlineEnumArrayDtoBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class Issue280Test {

  @Test
  void builder_when_used_then_compilesAndReturnsEnumConstants() {
    final InlineEnumArrayDto dto =
        fullInlineEnumArrayDtoBuilder().setData(Arrays.asList(HELLO, WORLD)).build();

    assertEquals(
        "HELLO|WORLD",
        dto.getDataOpt()
            .map(
                l ->
                    l.stream()
                        .map(InlineEnumArrayDto.DataEnum::getValue)
                        .collect(Collectors.joining("|")))
            .orElse(""));
  }
}
