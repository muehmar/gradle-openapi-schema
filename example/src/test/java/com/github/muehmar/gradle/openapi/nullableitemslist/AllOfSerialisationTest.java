package com.github.muehmar.gradle.openapi.nullableitemslist;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class AllOfSerialisationTest {
  private static final ObjectMapper OBJECT_MAPPER = MapperFactory.mapper();

  @Test
  void writeValueAsString_when_dtoSetterUsed_then_matchJson() throws JsonProcessingException {
    final UserDto userDto =
        UserDto.fullUserDtoBuilder()
            .setIds(Collections.singletonList("id-1234"))
            .setUsernames(Collections.singletonList("user-1234"))
            .setEmails(Collections.singletonList("email-1234"))
            .setPhones(Collections.singletonList("phone-1234"))
            .build();

    final SuperUserAllOfDto superUserAllOfDto =
        SuperUserAllOfDto.fullSuperUserAllOfDtoBuilder().setSuperUserId("super-user-id").build();

    final SuperUserDto superUserDto =
        SuperUserDto.fullSuperUserDtoBuilder()
            .setUserDto(userDto)
            .setSuperUserAllOfDto(superUserAllOfDto)
            .build();

    assertEquals(
        "{\"emails\":[\"email-1234\"],\"ids\":[\"id-1234\"],\"phones\":[\"phone-1234\"],\"superUserId\":\"super-user-id\",\"usernames\":[\"user-1234\"]}",
        OBJECT_MAPPER.writeValueAsString(superUserDto));
  }

  @Test
  void writeValueAsString_when_memberSettersUsed_then_matchJson() throws JsonProcessingException {
    final SuperUserDto superUserDto =
        SuperUserDto.fullSuperUserDtoBuilder()
            .setIds(Collections.singletonList("id-1234"))
            .setUsernames(Collections.singletonList("user-1234"))
            .setEmails(Collections.singletonList("email-1234"))
            .setPhones(Collections.singletonList("phone-1234"))
            .setSuperUserId("super-user-id")
            .build();

    assertEquals(
        "{\"emails\":[\"email-1234\"],\"ids\":[\"id-1234\"],\"phones\":[\"phone-1234\"],\"superUserId\":\"super-user-id\",\"usernames\":[\"user-1234\"]}",
        OBJECT_MAPPER.writeValueAsString(superUserDto));
  }

  @Test
  void writeValueAsString_when_overloadMemberSettersUsed_then_matchJson()
      throws JsonProcessingException {
    final SuperUserDto superUserDto =
        SuperUserDto.fullSuperUserDtoBuilder()
            .setIds_(Collections.singletonList(Optional.of("id-1234")))
            .setUsernames_(Collections.singletonList(Optional.of("user-1234")))
            .setEmails_(Collections.singletonList(Optional.of("email-1234")))
            .setPhones_(Collections.singletonList(Optional.of("phone-1234")))
            .setSuperUserId("super-user-id")
            .build();

    assertEquals(
        "{\"emails\":[\"email-1234\"],\"ids\":[\"id-1234\"],\"phones\":[\"phone-1234\"],\"superUserId\":\"super-user-id\",\"usernames\":[\"user-1234\"]}",
        OBJECT_MAPPER.writeValueAsString(superUserDto));
  }
}
