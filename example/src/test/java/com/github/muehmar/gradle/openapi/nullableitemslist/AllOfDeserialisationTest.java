package com.github.muehmar.gradle.openapi.nullableitemslist;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.Tristate;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class AllOfDeserialisationTest {
  private static final ObjectMapper OBJECT_MAPPER = MapperFactory.mapper();

  @Test
  void readValue_when_allListsHaveValues_then_gettersReturnExpectedValues()
      throws JsonProcessingException {
    final String json =
        "{\"emails\":[\"email-1234\"],\"ids\":[\"id-1234\"],\"phones\":[\"phone-1234\"],\"superUserId\":\"super-user-id\",\"usernames\":[\"user-1234\"]}";
    final SuperUserDto superUserDto = OBJECT_MAPPER.readValue(json, SuperUserDto.class);

    assertEquals(Collections.singletonList(Optional.of("id-1234")), superUserDto.getIds());
    assertEquals(
        Collections.singletonList(Optional.of("user-1234")),
        superUserDto.getUsernamesOr(Collections.emptyList()));
    assertEquals(
        Optional.of(Collections.singletonList(Optional.of("user-1234"))),
        superUserDto.getUsernamesOpt());
    assertEquals(
        Optional.of(Collections.singletonList(Optional.of("email-1234"))),
        superUserDto.getEmailsOpt());
    assertEquals(
        Collections.singletonList(Optional.of("email-1234")),
        superUserDto.getEmailsOr(Collections.emptyList()));
    assertEquals(
        Tristate.ofValue(Collections.singletonList(Optional.of("phone-1234"))),
        superUserDto.getPhonesTristate());
    assertEquals(Optional.of("super-user-id"), superUserDto.getSuperUserIdOpt());
  }
}
