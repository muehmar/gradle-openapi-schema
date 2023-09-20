package com.github.muehmar.gradle.openapi.allof;

import static openapischema.example.api.allof.model.AllOfMemberReferenceDto.allOfMemberReferenceDtoBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import java.util.UUID;
import openapischema.example.api.allof.model.AllOfMemberReferenceDto;
import org.junit.jupiter.api.Test;

class AllOfMemberReferenceTest {

  @Test
  void idIsResolvedAsUUIDAndNotAsDto() {
    final UUID id = UUID.randomUUID();
    final AllOfMemberReferenceDto dto =
        allOfMemberReferenceDtoBuilder().setId(id).setName("name").build();

    assertEquals(Optional.of(id), dto.getIdOpt());
  }
}
