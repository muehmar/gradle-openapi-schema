package com.github.muehmar.gradle.openapi.allof;

import static com.github.muehmar.gradle.openapi.allof.AllOfMemberReferenceDto.allOfMemberReferenceDtoBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AllOfMemberReferenceTest {

  @Test
  void idIsResolvedAsUUIDAndNotAsDto() {
    final UUID id = UUID.randomUUID();
    final AllOfMemberReferenceDto dto =
        allOfMemberReferenceDtoBuilder().setId(id).setValue("value").setName("name").build();

    assertEquals(Optional.of(id), dto.getIdOpt());
  }
}
