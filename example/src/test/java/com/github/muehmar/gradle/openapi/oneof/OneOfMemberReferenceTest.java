package com.github.muehmar.gradle.openapi.oneof;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OneOfMemberReferenceTest {
  @Test
  void idIsResolvedAsUUIDAndNotAsDto() {
    final UUID id = UUID.randomUUID();

    final BaseOneOfMemberReferenceDto baseDto =
        BaseOneOfMemberReferenceDto.builder().andAllOptionals().setId(id).build();

    final OneOfMemberReferenceDto dto =
        OneOfMemberReferenceDto.builder().setBaseOneOfMemberReferenceDto(baseDto).build();

    dto.foldOneOf(
        base -> {
          assertEquals(Optional.of(id), base.getIdOpt());
          return true;
        },
        ignore -> {
          fail();
          return false;
        });
  }
}
