package com.github.muehmar.gradle.openapi.nullableitemslist;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.Tristate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class DeserialisationTest {
  private static final JsonMapper OBJECT_MAPPER = MapperFactory.jsonMapper();

  @Test
  void readValue_when_allListsHaveValues_then_gettersReturnExpectedValues() throws Exception {
    final String json =
        "{\"emails\":[\"email-1234\"],\"ids\":[\"id-1234\"],\"phones\":[\"phone-1234\"],\"usernames\":[\"user-1234\"]}";
    final UserDto userDto = OBJECT_MAPPER.readValue(json, UserDto.class);

    assertEquals(Collections.singletonList(Optional.of("id-1234")), userDto.getIds());
    assertEquals(
        Collections.singletonList(Optional.of("user-1234")),
        userDto.getUsernamesOr(Collections.emptyList()));
    assertEquals(
        Optional.of(Collections.singletonList(Optional.of("user-1234"))),
        userDto.getUsernamesOpt());
    assertEquals(
        Optional.of(Collections.singletonList(Optional.of("email-1234"))), userDto.getEmailsOpt());
    assertEquals(
        Collections.singletonList(Optional.of("email-1234")),
        userDto.getEmailsOr(Collections.emptyList()));
    assertEquals(
        Tristate.ofValue(Collections.singletonList(Optional.of("phone-1234"))),
        userDto.getPhonesTristate());
  }

  @Test
  void readValue_when_allListsHaveValues_then_matchExpectedDto() throws Exception {
    final String json =
        "{\"emails\":[\"email-1234\"],\"ids\":[\"id-1234\"],\"phones\":[\"phone-1234\"],\"usernames\":[\"user-1234\"]}";
    final UserDto deserialisedDto = OBJECT_MAPPER.readValue(json, UserDto.class);

    final UserDto userDto =
        UserDto.fullUserDtoBuilder()
            .setIds(Collections.singletonList("id-1234"))
            .setUsernames(Collections.singletonList("user-1234"))
            .setEmails(Collections.singletonList("email-1234"))
            .setPhones(Collections.singletonList("phone-1234"))
            .build();

    assertEquals(userDto, deserialisedDto);
  }

  @Test
  void readValue_when_listHaveMixedValues_then_matchExpectedDto() throws Exception {
    final String json =
        "{\"emails\":[\"email-1234\", null],\"ids\":[null, \"id-1234\"],\"phones\":[\"phone-1234\", null, \"phone-5678\"],\"usernames\":[\"user-5678\", null,\"user-1234\"]}";
    final UserDto deserialisedDto = OBJECT_MAPPER.readValue(json, UserDto.class);

    final UserDto userDto =
        UserDto.fullUserDtoBuilder()
            .setIds_(Arrays.asList(Optional.empty(), Optional.of("id-1234")))
            .setUsernames_(
                Arrays.asList(Optional.of("user-5678"), Optional.empty(), Optional.of("user-1234")))
            .setEmails_(Arrays.asList(Optional.of("email-1234"), Optional.empty()))
            .setPhones_(
                Arrays.asList(
                    Optional.of("phone-1234"), Optional.empty(), Optional.of("phone-5678")))
            .build();

    assertEquals(userDto, deserialisedDto);
  }

  @Test
  void readValue_when_emptyOrNullLists_then_matchExpectedDto() throws Exception {
    final String json = "{\"ids\":[\"id-1234\"],\"phones\":null,\"usernames\":null}";
    final UserDto deserialisedDto = OBJECT_MAPPER.readValue(json, UserDto.class);

    final UserDto userDto =
        UserDto.fullUserDtoBuilder()
            .setIds(Collections.singletonList("id-1234"))
            .setUsernames(Optional.empty())
            .setEmails(Optional.empty())
            .setPhones(Tristate.ofNull())
            .build();

    assertEquals(userDto, deserialisedDto);
  }

  @Test
  void readValue_when_absentTristate_then_matchExpectedDto() throws Exception {
    final String json = "{\"ids\":[\"id-1234\"],\"usernames\":null}";
    final UserDto deserialisedDto = OBJECT_MAPPER.readValue(json, UserDto.class);
    final UserDto userDto =
        UserDto.fullUserDtoBuilder()
            .setIds(Collections.singletonList("id-1234"))
            .setUsernames(Optional.empty())
            .setEmails(Optional.empty())
            .setPhones(Tristate.ofAbsent())
            .build();

    assertEquals(userDto, deserialisedDto);
  }
}
