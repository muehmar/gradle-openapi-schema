package com.github.muehmar.gradle.openapi.anyof;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import openapischema.example.api.anyof.model.AnyOfMemberReferenceDto;
import openapischema.example.api.anyof.model.BaseAnyOfMemberReferenceDto;
import org.junit.jupiter.api.Test;

class AnyOfMemberReferenceTest {
  @Test
  void idIsResolvedAsUUIDAndNotAsDto() {
    final UUID id = UUID.randomUUID();
    final BaseAnyOfMemberReferenceDto baseDto =
        BaseAnyOfMemberReferenceDto.builder().andAllOptionals().setId(id).build();

    final AnyOfMemberReferenceDto dto =
        AnyOfMemberReferenceDto.builder().setBaseAnyOfMemberReferenceDto(baseDto).build();

    final List<Boolean> booleans =
        dto.foldAnyOf(
            base -> {
              assertEquals(Optional.of(id), base.getIdOpt());
              return true;
            },
            ignore -> false);
    assertArrayEquals(new Object[] {true, false}, booleans.toArray());
  }
}
